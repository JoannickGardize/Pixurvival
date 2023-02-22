package com.pixurvival.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.*;
import com.pixurvival.core.LoadGameException.Reason;
import com.pixurvival.core.World.Type;
import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackContext;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.serialization.ContentPackValidityCheckResult;
import com.pixurvival.core.livingEntity.ItemCraftDiscoveryListener;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.map.analytics.MapAnalyticsException;
import com.pixurvival.core.map.chunk.ChunkManagerPlugin;
import com.pixurvival.core.message.*;
import com.pixurvival.core.message.lobby.*;
import com.pixurvival.core.message.playerRequest.IPlayerActionRequest;
import com.pixurvival.core.system.SystemData;
import com.pixurvival.core.util.*;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// TODO Couper cette classe en deux implémentations distinctes : NetworkClientGame et LocalClientGame
public class PixurvivalClient extends PluginHolder<PixurvivalClient> implements CommandExecutor, WorldListener, ItemCraftDiscoveryListener {

    private Client client = new Client(WorldUpdate.BUFFER_SIZE * 2, WorldUpdate.BUFFER_SIZE * 2, new KryoSerialization(new WorldKryo()));
    private NetworkMessageHandler clientListener;
    private List<ClientGameListener> listeners = new ArrayList<>();
    private @Getter World world = null;
    private @Getter ContentPackContext contentPackContext;
    private @Getter ContentPackDownloader contentPackDownloadManager = new ContentPackDownloader(this);
    private List<IPlayerActionRequest> playerActionRequests = new ArrayList<>();

    private float deltaTimeMillis = 0;
    private String[] gameBeginningCommands;
    private @Getter
    @Setter List<Locale> localePriorityList = new ArrayList<>();
    private @Getter Locale currentLocale;
    private @Getter boolean spectator;
    private @Getter int myTeamId = 1;
    private @Getter long myOriginalPlayerId;
    private ContentPackIdentifier waitingContentPack;
    private SingleplayerLobby singlePlayerLobby;

    public PixurvivalClient(CommonMainArgs clientArgs, Supplier<Collection<ChunkManagerPlugin>> chunkManagerPluginSupplier) {
        KryoInitializer.apply(client.getKryo());
        contentPackContext = new ContentPackContext(new File(clientArgs.getContentPackDirectory()));
        clientListener = new NetworkMessageHandler(this, chunkManagerPluginSupplier);
        clientArgs.apply(client, clientListener);
        gameBeginningCommands = clientArgs.getGameBeginingCommands();
        localePriorityList.add(Locale.getDefault());
        if (!Locale.getDefault().equals(new Locale("en", "US"))) {
            localePriorityList.add(new Locale("en", "US"));
        }
    }

    public void setWorld(World world) {
        this.world = world;
        world.addListener(this);
        ((WorldKryo) client.getKryo()).setWorld(world);
    }

    public void addListener(ClientGameListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ClientGameListener listener) {
        listeners.remove(listener);
    }

    void notify(Consumer<ClientGameListener> action) {
        listeners.forEach(action);
    }

    public PlayerEntity getMyPlayer() {
        return world.getMyPlayer();
    }

    public PlayerInventory getMyInventory() {
        return world.getMyPlayer().getInventory();
    }

    public void spectate(Spectate spectate) {
        focusPlayer(spectate.getPlayerId(), spectate.getPlayerPosition(), true);
    }

    public void respawn(Respawn respawn) {
        focusPlayer(respawn.getPlayerId(), respawn.getPlayerPosition(), false);
    }

    private void focusPlayer(long playerId, Vector2 newPosition, boolean spectator) {
        PlayerEntity player = world.getPlayerEntities().get(playerId);
        player.getPosition().set(newPosition);
        player.setInventory(world.getMyPlayer().getInventory());
        this.spectator = spectator;
        world.setMyPlayer(player);
        listeners.forEach(ClientGameListener::playerFocusChanged);
    }

    public void connectToServer(String address, int port, String playerName) {
        try {
            disconnectFromServer();
            client.start();
            client.connect(5000, address, port, port);
            client.sendTCP(new LoginRequest(playerName, ReleaseVersion.actual().name()));
        } catch (Exception e) {
            Log.error("Error when trying to connect to server.", e);
            listeners.forEach(l -> l.loginResponse(new LoginResponse("Unable to connect to server.")));
        }
    }

    public void disconnectFromServer() {
        if (client.isConnected()) {
            client.stop();
            client.close();
        }
    }

    public void dispose() {
        if (world != null) {
            world.unload();
        }
        disconnectFromServer();
        try {
            client.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Locale getLocaleFor(ContentPack contentPack) {
        if (contentPack == null) {
            return localePriorityList.get(0);
        } else {
            return LocaleUtils.findBestMatch(localePriorityList, contentPack.getTranslations().keySet());
        }
    }

    public Locale getLocaleFor(Collection<String> localTags) {
        return LocaleUtils.findBestMatch(localePriorityList, LocaleUtils.toLocale(localTags));
    }

    public void checkContentPackValidity(ContentPackCheck check) {
        ContentPackValidityCheckResult result;
        try {
            result = contentPackContext.checkSameness(check.getIdentifier(), check.getChecksum());
            if (result == ContentPackValidityCheckResult.OK) {
                client.sendTCP(new ContentPackReady(check.getIdentifier()));
            } else {
                listeners.forEach(l -> l.questionDownloadContentPack(check.getIdentifier(), result));
            }
        } catch (ContentPackException e) {
            listeners.forEach(l -> l.questionDownloadContentPack(check.getIdentifier(), ContentPackValidityCheckResult.NOT_FOUND));
        }
    }

    public void refuseContentPack(ContentPackIdentifier identifier) {
        client.sendTCP(new RefuseContentPack(identifier));
    }

    public void acceptContentPack(ContentPackIdentifier identifier) {
        waitingContentPack = identifier;
        client.sendTCP(new ContentPackRequest(identifier));
    }

    public void createClientWorld(CreateWorld createWorld, Collection<ChunkManagerPlugin> chunkManagerPlugins) {
        try {
            myTeamId = createWorld.getMyTeamId();
            myOriginalPlayerId = createWorld.getMyOriginalPlayerId();
            setWorld(World.createClientWorld(createWorld, contentPackContext, chunkManagerPlugins));
            world.addPlugin(new WorldUpdateManager(this));
            currentLocale = getLocaleFor(world.getContentPack());
            removeAllPlugins();
            if (createWorld.getPlayerDeadIds() != null) {
                for (long id : createWorld.getPlayerDeadIds()) {
                    PlayerEntity playerEntity = world.getPlayerEntities().get(id);
                    playerEntity.getTeam().addDead(playerEntity);
                    playerEntity.setAlive(false);
                }
            }
            spectator = createWorld.isSpectator();
            notify(ClientGameListener::initializeGame);
            if (spectator) {
                notify(ClientGameListener::playerFocusChanged);
            }
            world.initializeClientGame(createWorld);
            discovered(createWorld.getDiscoveredItemCrafts());
        } catch (ContentPackException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }

    public void startNewLocalGame(String saveName, Collection<ChunkManagerPlugin> chunkManagerPlugins) throws LoadGameException {
        if (singlePlayerLobby == null) {
            throw new IllegalStateException("No SingleplayerLobby to initialize the local game");
        }
        if (singlePlayerLobby.getSelectedGameModeIndex() == -1) {
            Log.warn("No GameMode selected");
            return;
        }
        removeAllPlugins();
        ContentPack localGamePack;
        try {
            localGamePack = contentPackContext.load(singlePlayerLobby.getSelectedContentPackIdentifier());
        } catch (ContentPackException e) {
            throw new LoadGameException(Reason.PARSE_EXCEPTION, e.getMessage());
        }
        ReleaseVersion packVersion = ReleaseVersion.valueFor(localGamePack.getReleaseVersion());
        if (!ReleaseVersion.actual().isContentPackCompatibleWith(packVersion)) {
            throw new LoadGameException(Reason.INCOMPATIBLE_CONTENT_PACK_VERSION, packVersion, ReleaseVersion.actual());
        }
        if (!contentPackContext.getErrors(localGamePack).isEmpty()) {
            throw new LoadGameException(Reason.CONTAINS_ERRORS);
        }
        currentLocale = getLocaleFor(localGamePack);
        setWorld(World.createNewLocalWorld(localGamePack, singlePlayerLobby.getSelectedGameModeIndex(), chunkManagerPlugins));
        world.setSaveName(saveName);
        GameMode gameMode = world.getGameMode();
        if (gameMode.getTeamNumberInterval().getMin() > 1 || gameMode.getTeamSizeInterval().getMin() > 1) {
            throw new LoadGameException(Reason.NOT_PLAYABLE_IN_SOLO);
        }
        try {
            world.initializeNewGame();
        } catch (MapAnalyticsException e) {
            throw new LoadGameException(Reason.OTHER, e.getMessage());
        }
        world.getMyPlayer().addItemCraftDiscoveryListener(this);
        myOriginalPlayerId = world.getMyPlayer().getId();
        notify(ClientGameListener::initializeGame);
        addPlugin(new WorldUpdater());
        for (String command : gameBeginningCommands) {
            world.getCommandManager().process(this, CommandArgsUtils.splitArgs(command));
        }
        singlePlayerLobby = null;
        notify(ClientGameListener::gameStarted);
    }

    public void loadAndStartLocalGame(String saveName, Collection<ChunkManagerPlugin> chunkManagerPlugins) throws LoadGameException {
        try {
            setWorld(WorldSerialization.load(saveName, contentPackContext, chunkManagerPlugins));
            currentLocale = getLocaleFor(world.getContentPack());
            world.initializeLoadedGame();
            notify(ClientGameListener::initializeGame);
            world.getMyPlayer().addItemCraftDiscoveryListener(this);
            myOriginalPlayerId = world.getMyPlayer().getId();
            addPlugin(new WorldUpdater());
            singlePlayerLobby = null;
            notify(ClientGameListener::gameStarted);
        } catch (IOException e) {
            throw new LoadGameException(Reason.PARSE_EXCEPTION, e.getMessage());
        }
    }

    public SingleplayerLobby getSinglePlayerLobby() {
        if (singlePlayerLobby == null) {
            singlePlayerLobby = new SingleplayerLobby(this);
        }
        return singlePlayerLobby;
    }

    public LobbyData getSinglePlayerLobbyData() {
        return getSinglePlayerLobby().getLobbyData();
    }

    public void sendAction(IPlayerActionRequest request) {
        if (world != null) {
            if (world.getType() == World.Type.CLIENT) {
                // TODO TCP for some actions
                client.sendUDP(request);
                if (request.isClientPreapply() && world.getMyPlayer().getId() == myOriginalPlayerId) {
                    synchronized (playerActionRequests) {
                        playerActionRequests.add(request);
                    }
                }
            } else {
                synchronized (playerActionRequests) {
                    playerActionRequests.add(request);
                }
            }
        }
    }

    public void send(ClientStream clientStream) {
        client.sendUDP(clientStream);
    }

    public void update(float deltaTimeMillis) {
        this.deltaTimeMillis = deltaTimeMillis;
        clientListener.consumeReceivedObjects();
        updatePlugins(this);
    }

    public void updateWorld() {
        if (world != null) {
            if (getMyPlayer() != null) {
                synchronized (playerActionRequests) {
                    playerActionRequests.forEach(r -> r.apply(getMyPlayer()));
                    playerActionRequests.clear();
                }
            }
            world.update(deltaTimeMillis);
        }
    }

    public void synchronizeTime(TimeSync timeResponse) {
        if (world != null) {
            world.getTime().synchronizeTime(timeResponse);
        }
    }

    public void sendGameReady() {
        client.sendTCP(new GameReady());
    }

    public void requestRefresh() {
        client.sendUDP(new RefreshRequest());
    }

    public void offer(WorldUpdate worldUpdate) {
        world.getPlugin(WorldUpdateManager.class).offer(worldUpdate);
    }

    @Override
    public boolean isOperator() {
        return true;
    }

    @Override
    public void gameEnded(EndGameData data) {
        notifyGameEnded(data);
    }

    void notifyGameEnded(EndGameData data) {
        listeners.forEach(l -> l.gameEnded(data));
    }

    public void send(LobbyRequest request) {
        if (client.isConnected()) {
            client.sendTCP(request);
        } else if (request instanceof ChooseGameModeRequest) {
            getSinglePlayerLobby().handle((ChooseGameModeRequest) request);
        }
    }

    public void notifyContentPackAvailable(ContentPackIdentifier identifier) {
        if (identifier.equals(waitingContentPack)) {
            waitingContentPack = null;
            client.sendTCP(new ContentPackReady(identifier));
        }
        listeners.forEach(l -> l.contentPackAvailable(identifier));
    }

    public void requestPause(boolean pause) {
        if (world.getType() == Type.LOCAL) {
            if (pause) {
                removePlugin(WorldUpdater.class);
            } else {
                addPlugin(new WorldUpdater());
            }
        }
    }

    @Override
    public void discovered(Collection<ItemCraft> itemCrafts) {
        notify(l -> l.discovered(itemCrafts));
    }

    public void discovered(int[] itemCraftIds) {
        if (world != null) {
            discovered(Arrays.stream(itemCraftIds).mapToObj(world.getContentPack().getItemCrafts()::get).collect(Collectors.toList()));
        }
    }

    public PlayerEntity getMyOriginalPlayerEntity() {
        return world.getPlayerEntities().get(myOriginalPlayerId);
    }

    public void handleSystemData(SystemData data) {
        if (world != null) {
            world.getSystem(data.systemOwnerType()).accept(data);
        }
    }
}
