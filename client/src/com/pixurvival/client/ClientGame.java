package com.pixurvival.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.World;
import com.pixurvival.core.World.Type;
import com.pixurvival.core.WorldListener;
import com.pixurvival.core.WorldUpdateManager;
import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.Version;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.serialization.ContentPackSerializer;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.GameReady;
import com.pixurvival.core.message.KryoInitializer;
import com.pixurvival.core.message.LoginRequest;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.RefreshRequest;
import com.pixurvival.core.message.Spectate;
import com.pixurvival.core.message.TimeRequest;
import com.pixurvival.core.message.TimeResponse;
import com.pixurvival.core.message.WorldReady;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.message.playerRequest.IPlayerActionRequest;
import com.pixurvival.core.util.CommonMainArgs;
import com.pixurvival.core.util.LocaleUtils;
import com.pixurvival.core.util.PluginHolder;

import lombok.Getter;
import lombok.Setter;

// TODO Couper cette classe en deux implémentations distinctes : NetworkClientGame et LocalClientGame
public class ClientGame extends PluginHolder<ClientGame> implements CommandExecutor, WorldListener {

	private Client client = new Client(8192, 8192);
	private NetworkMessageHandler clientListener;
	private List<ClientGameListener> listeners = new ArrayList<>();
	private @Getter World world = null;
	private @Getter ContentPackDownloadManager contentPackDownloadManager = new ContentPackDownloadManager();
	private ContentPackSerializer contentPackSerializer = new ContentPackSerializer(new File("contentPacks"));
	private List<IPlayerActionRequest> playerActionRequests = new ArrayList<>();

	private long timeRequestFrequencyMillis = 1000;
	private double timeRequestTimer = 0;
	private double deltaTimeMillis = 0;
	private String[] gameBeginningCommands;
	private @Getter @Setter List<Locale> localePriorityList = new ArrayList<>();
	private @Getter Locale currentLocale;
	private @Getter boolean spectator;
	private @Getter int myTeamId;

	public ClientGame(CommonMainArgs clientArgs) {
		client = new Client(8192, 8192);
		KryoInitializer.apply(client.getKryo());
		clientListener = new NetworkMessageHandler(this);
		clientArgs.apply(client, clientListener);
		if (clientArgs.getOnGameBeginning() != null) {
			gameBeginningCommands = clientArgs.getOnGameBeginning().split(";");
		} else {
			gameBeginningCommands = new String[0];
		}
		localePriorityList.add(Locale.getDefault());
		if (!Locale.getDefault().equals(Locale.US)) {
			localePriorityList.add(Locale.US);
		}
	}

	public void setWorld(World world) {
		this.world = world;
		world.addListener(this);
	}

	public void addListener(ClientGameListener listener) {
		listeners.add(listener);
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
			if (client.isConnected()) {
				client.stop();
				client.close();
			}
			client.start();
			client.connect(5000, address, port, port);
			client.sendTCP(new LoginRequest(playerName));
		} catch (Exception e) {
			Log.error("Error when trying to connect to server.", e);
			listeners.forEach(l -> l.loginResponse(LoginResponse.INTERNAL_ERROR));
		}
	}

	public void initializeNetworkWorld(CreateWorld createWorld) {
		try {
			myTeamId = createWorld.getMyTeamId();
			createWorld.getInventory().computeQuantities();
			setWorld(World.createClientWorld(createWorld, contentPackSerializer));
			currentLocale = LocaleUtils.findBestMatch(localePriorityList, world.getContentPack().getTranslations().keySet());
			removeAllPlugins();
			notify(ClientGameListener::initializeGame);
			addPlugin(new TargetPositionUpdateManager());
			client.sendTCP(new WorldReady());
		} catch (ContentPackException e) {
			e.printStackTrace();
		}
	}

	public void startLocalGame(int gameModeId) {
		removeAllPlugins();
		ContentPack localGamePack;
		try {
			localGamePack = contentPackSerializer.load(new ContentPackIdentifier("Vanilla", new Version(1, 0)));
		} catch (ContentPackException e) {
			e.printStackTrace();
			return;
		}
		currentLocale = LocaleUtils.findBestMatch(localePriorityList, localGamePack.getTranslations().keySet());
		setWorld(World.createLocalWorld(localGamePack, gameModeId));
		GameMode gameMode = world.getGameMode();
		if (gameMode.getTeamNumberInterval().getMin() > 1 || gameMode.getTeamSizeInterval().getMin() > 1) {
			throw new IllegalStateException("The GameMode + " + gameMode.getName() + " cannot be played in solo.");
		}
		world.initializeGame();
		notify(ClientGameListener::initializeGame);
		addPlugin(new WorldUpdater());
		for (String command : gameBeginningCommands) {
			world.getCommandManager().process(this, CommandArgsUtils.splitArgs(command));
		}
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

	public void update(double deltaTimeMillis) {
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
			if (world.getType() == Type.CLIENT) {
				timeRequestTimer -= deltaTimeMillis;
				if (timeRequestTimer <= 0) {
					timeRequestTimer = timeRequestFrequencyMillis;
					client.sendUDP(new TimeRequest(world.getTime().getTimeMillis()));
				}
			}
		}
	}

	public void checkMissingPacks(ContentPackIdentifier identifier) {
		// TODO Make the auto-download of contentPack great again

		// List<ContentPackIdentifier> missingPacks = new ArrayList<>();
		// for (ContentPackIdentifier identifier : identifiers) {
		// if (!list.contains(identifier)) {
		// missingPacks.add(identifier);
		// }
		// }
		// if (!missingPacks.isEmpty()) {
		// client.sendTCP(
		// new RequestContentPacks(missingPacks.toArray(new
		// ContentPackIdentifier[missingPacks.size()])));
		// }
		// contentPackDownloadManager.setMissingList(missingPacks);
	}

	public void synchronizeTime(TimeResponse timeResponse) {
		if (world != null) {
			timeRequestFrequencyMillis = world.getTime().synchronizeTime(timeResponse);
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
}
