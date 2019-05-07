package com.pixurvival.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.ContentPackLoader;
import com.pixurvival.core.contentPack.Version;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.GameReady;
import com.pixurvival.core.message.InitializeGame;
import com.pixurvival.core.message.KryoInitializer;
import com.pixurvival.core.message.LoginRequest;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.PlayerData;
import com.pixurvival.core.message.TimeRequest;
import com.pixurvival.core.message.TimeResponse;
import com.pixurvival.core.message.WorldReady;
import com.pixurvival.core.message.playerRequest.IPlayerActionRequest;
import com.pixurvival.core.util.CommonMainArgs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ClientGame {

	private Client client = new Client(8192, 8192);
	private ClientListener clientListener;
	private List<ClientGameListener> listeners = new ArrayList<>();
	private @Getter @Setter(AccessLevel.PACKAGE) World world = null;
	private @Getter ContentPackDownloadManager contentPackDownloadManager = new ContentPackDownloadManager();
	private @Getter PlayerInventory myInventory;
	private ContentPackLoader contentPackLoader = new ContentPackLoader(new File("contentPacks"));
	private List<IPlayerActionRequest> playerActionRequests = new ArrayList<>();
	private List<IClientGamePlugin> plugins = new ArrayList<>();

	private long timeRequestFrequencyMillis = 1000;
	private double timeRequestTimer = 0;

	public ClientGame(CommonMainArgs clientArgs) {
		client = new Client(8192, 8192);
		KryoInitializer.apply(client.getKryo());
		clientListener = new ClientListener(this);
		clientArgs.apply(client, clientListener);
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
			setWorld(World.createClientWorld(createWorld, contentPackLoader));
			client.sendTCP(new WorldReady());
		} catch (ContentPackException e) {
			e.printStackTrace();
		}
	}

	public void initializeNetworkGame(InitializeGame initGame) {
		world.setMyPlayerId(initGame.getMyPlayerId());
		initGame.getInventory().computeQuantities();
		myInventory = initGame.getInventory();
		world.addPlayerData(Arrays.asList(initGame.getPlayerData()));
		notify(ClientGameListener::initializeGame);
		plugins.clear();
		plugins.add(new TargetPositionUpdateManager());
		initializePlugins();
	}

	public void startLocalGame() {
		ContentPack localGamePack;
		try {
			localGamePack = contentPackLoader.load(new ContentPackIdentifier("Vanilla", new Version(1, 0)));
		} catch (ContentPackException e) {
			e.printStackTrace();
			return;
		}
		this.world = World.createLocalWorld(localGamePack);
		PlayerEntity playerEntity = new PlayerEntity();
		// TODO
		playerEntity.getPosition().set(0, 0);
		world.getEntityPool().add(playerEntity);

		CreatureEntity creature = new CreatureEntity(localGamePack.getCreatures().get(0));
		creature.getPosition().set(15, 15);
		world.getEntityPool().add(creature);

		Random random = new Random();
		for (int i = 0; i < 20; i++) {
			playerEntity.getInventory().setSlot(i, new ItemStack(localGamePack.getItems().get(random.nextInt(localGamePack.getItems().size())), random.nextInt(10) + 1));
		}
		world.setMyPlayerId(playerEntity.getId());
		myInventory = playerEntity.getInventory();

		ItemStackEntity itemStackEntity = new ItemStackEntity(new ItemStack(localGamePack.getItems().get(0)));
		itemStackEntity.getPosition().set(10, 10);
		world.getEntityPool().add(itemStackEntity);

		initializePlugins();

		notify(ClientGameListener::initializeGame);
	}

	public void sendAction(IPlayerActionRequest request) {
		if (world != null) {
			if (world.getType() == World.Type.CLIENT) {
				client.sendUDP(request);
				if (getMyPlayer() != null && request.isClientPreapply()) {
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
		for (IClientGamePlugin plugin : plugins) {
			plugin.update(this);
		}
		synchronized (playerActionRequests) {
			playerActionRequests.forEach(r -> r.apply(getMyPlayer()));
			playerActionRequests.clear();
		}
		clientListener.consumeReceivedObjects();
		if (world != null) {
			if (getMyPlayer() != null && getMyPlayer().getInventory() == null) {
				getMyPlayer().setInventory(myInventory);
			}
			world.update(deltaTimeMillis);
			if (world.isClient()) {
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

	public void notifyReady() {
		client.sendTCP(new GameReady());
	}

	public void addPlayerData(PlayerData[] data) {
		if (world != null) {
			world.addPlayerData(Arrays.asList(data));
		}
	}

	private void initializePlugins() {
		for (IClientGamePlugin plugin : plugins) {
			plugin.initialize(this);
		}
	}
}
