package com.pixurvival.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.ContentPackLoader;
import com.pixurvival.core.contentPack.Version;
import com.pixurvival.core.entity.EntityGroup;
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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ClientGame {

	private Client client = new Client(8192, 8192);
	private ClientListener clientListener;
	private List<ClientGameListener> listeners = new ArrayList<>();
	private @Getter @Setter(AccessLevel.PACKAGE) World world = null;
	private @Getter long myPlayerId;
	private @Getter ContentPackDownloadManager contentPackDownloadManager = new ContentPackDownloadManager();
	private @Getter PlayerInventory myInventory;
	private ContentPackLoader contentPackLoader = new ContentPackLoader(new File("contentPacks"));
	private List<IPlayerActionRequest> playerActionRequests = new ArrayList<>();

	private long timeRequestFrequencyMillis = 1000;
	private double timeRequestTimer = 0;

	public ClientGame() {
		client = new Client(8192, 8192);
		KryoInitializer.apply(client.getKryo());
		clientListener = new ClientListener(this);
		// TODO enlever lag simulation
		client.addListener(new Listener.LagListener(40, 50, clientListener));
	}

	public void addListener(ClientGameListener listener) {
		listeners.add(listener);
	}

	void notify(Consumer<ClientGameListener> action) {
		listeners.forEach(action);
	}

	public PlayerEntity getMyPlayer() {
		return (PlayerEntity) world.getEntityPool().get(EntityGroup.PLAYER, myPlayerId);
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

	public void initializeWorld(CreateWorld createWorld) {
		try {
			setWorld(World.createClientWorld(createWorld, contentPackLoader));
			client.sendTCP(new WorldReady());
		} catch (ContentPackException e) {
			e.printStackTrace();
		}
	}

	public void initializeGame(InitializeGame initGame) {
		myPlayerId = initGame.getMyPlayerId();
		myInventory = initGame.getInventory();
		world.addPlayerData(initGame.getPlayerData());
		notify(ClientGameListener::initializeGame);
	}

	public void startLocalGame() {
		ContentPack localGamePack;
		try {
			localGamePack = contentPackLoader.load(new ContentPackIdentifier("Vanilla", new Version(1, 0)));
		} catch (ContentPackException e) {
			e.printStackTrace();
			return;
		}
		World world = World.createLocalWorld(localGamePack);
		this.world = world;
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
		myPlayerId = playerEntity.getId();
		myInventory = playerEntity.getInventory();

		ItemStackEntity itemStackEntity = new ItemStackEntity(new ItemStack(localGamePack.getItems().get(0)));
		itemStackEntity.getPosition().set(10, 10);
		world.getEntityPool().add(itemStackEntity);

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
			timeRequestTimer -= deltaTimeMillis;
			if (timeRequestTimer <= 0) {
				timeRequestTimer = timeRequestFrequencyMillis;
				client.sendUDP(new TimeRequest(world.getTime().getTimeMillis()));
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
			world.addPlayerData(data);
		}
	}
}
