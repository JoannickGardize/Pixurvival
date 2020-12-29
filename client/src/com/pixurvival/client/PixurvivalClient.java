package com.pixurvival.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.LoadGameException;
import com.pixurvival.core.LoadGameException.Reason;
import com.pixurvival.core.World;
import com.pixurvival.core.World.Type;
import com.pixurvival.core.WorldListener;
import com.pixurvival.core.WorldSerialization;
import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackContext;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.serialization.ContentPackValidityCheckResult;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.map.analytics.MapAnalyticsException;
import com.pixurvival.core.message.ClientStream;
import com.pixurvival.core.message.ContentPackCheck;
import com.pixurvival.core.message.ContentPackRequest;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.GameReady;
import com.pixurvival.core.message.KryoInitializer;
import com.pixurvival.core.message.LoginRequest;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.RefreshRequest;
import com.pixurvival.core.message.Spectate;
import com.pixurvival.core.message.TimeSync;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.message.lobby.ChooseGameModeRequest;
import com.pixurvival.core.message.lobby.ContentPackReady;
import com.pixurvival.core.message.lobby.LobbyData;
import com.pixurvival.core.message.lobby.LobbyRequest;
import com.pixurvival.core.message.lobby.RefuseContentPack;
import com.pixurvival.core.message.playerRequest.IPlayerActionRequest;
import com.pixurvival.core.util.CommonMainArgs;
import com.pixurvival.core.util.LocaleUtils;
import com.pixurvival.core.util.PluginHolder;
import com.pixurvival.core.util.ReleaseVersion;

import lombok.Getter;
import lombok.Setter;

// TODO Couper cette classe en deux implémentations distinctes : NetworkClientGame et LocalClientGame
public class PixurvivalClient extends PluginHolder<PixurvivalClient> implements CommandExecutor, WorldListener {

	private Client client = new Client(WorldUpdate.BUFFER_SIZE * 2, WorldUpdate.BUFFER_SIZE * 2);
	private NetworkMessageHandler clientListener;
	private List<ClientGameListener> listeners = new ArrayList<>();
	private @Getter World world = null;
	private @Getter ContentPackContext contentPackContext;
	private @Getter ContentPackDownloader contentPackDownloadManager = new ContentPackDownloader(this);
	private List<IPlayerActionRequest> playerActionRequests = new ArrayList<>();

	private float deltaTimeMillis = 0;
	private String[] gameBeginningCommands;
	private @Getter @Setter List<Locale> localePriorityList = new ArrayList<>();
	private @Getter Locale currentLocale;
	private @Getter boolean spectator;
	private @Getter int myTeamId = 1;
	private ContentPackIdentifier waitingContentPack;
	private SingleplayerLobby singlePlayerLobby;

	public PixurvivalClient(CommonMainArgs clientArgs) {
		KryoInitializer.apply(client.getKryo());
		contentPackContext = new ContentPackContext(new File(clientArgs.getContentPackDirectory()));
		clientListener = new NetworkMessageHandler(this);
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
		PlayerEntity player = world.getPlayerEntities().get(spectate.getPlayerId());
		player.getPosition().set(spectate.getPlayerPosition());
		player.setInventory(world.getMyPlayer().getInventory());
		spectator = true;
		world.setMyPlayer(player);
		listeners.forEach(ClientGameListener::spectatorStarted);
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
		List<Locale> locales = new ArrayList<>();
		for (String localTag : localTags) {
			locales.add(Locale.forLanguageTag(localTag));
		}
		return LocaleUtils.findBestMatch(localePriorityList, locales);
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

	public void initializeNetworkWorld(CreateWorld createWorld) {
		try {
			myTeamId = createWorld.getMyTeamId();
			setWorld(World.createClientWorld(createWorld, contentPackContext));
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
				notify(ClientGameListener::spectatorStarted);
			}
		} catch (ContentPackException e) {
			// TODO
			throw new RuntimeException(e);
		}
	}

	public void startNewLocalGame(String saveName) throws LoadGameException {
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
		if (ReleaseVersion.actual() != packVersion) {
			throw new LoadGameException(Reason.WRONG_CONTENT_PACK_VERSION, packVersion, ReleaseVersion.actual());
		}
		if (!contentPackContext.getErrors(localGamePack).isEmpty()) {
			throw new LoadGameException(Reason.CONTAINS_ERRORS);
		}
		currentLocale = getLocaleFor(localGamePack);
		setWorld(World.createLocalWorld(localGamePack, singlePlayerLobby.getSelectedGameModeIndex()));
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
		notify(ClientGameListener::initializeGame);
		addPlugin(new WorldUpdater());
		for (String command : gameBeginningCommands) {
			world.getCommandManager().process(this, CommandArgsUtils.splitArgs(command));
		}
		singlePlayerLobby = null;
		notify(ClientGameListener::gameStarted);
	}

	public void loadAndStartLocalGame(String saveName) throws LoadGameException {
		try {
			setWorld(WorldSerialization.load(saveName, contentPackContext));
			world.setSaveName(saveName);
			currentLocale = getLocaleFor(world.getContentPack());
			world.initializeLoadedGame();
			notify(ClientGameListener::initializeGame);
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
		if (world != null && getMyPlayer() != null && getMyPlayer().isAlive() && !spectator) {
			if (world.getType() == World.Type.CLIENT) {
				client.sendUDP(request);
				if (request.isClientPreapply()) {
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
}
