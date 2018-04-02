package com.pixurvival.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.World;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.aliveEntity.PlayerInventory;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.ContentPacksContext;
import com.pixurvival.core.contentPack.Version;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.map.Position;
import com.pixurvival.core.message.CraftItemRequest;
import com.pixurvival.core.message.DropItemRequest;
import com.pixurvival.core.message.GameReady;
import com.pixurvival.core.message.InitializeGame;
import com.pixurvival.core.message.InteractStructureRequest;
import com.pixurvival.core.message.InventoryActionRequest;
import com.pixurvival.core.message.KryoInitializer;
import com.pixurvival.core.message.LoginRequest;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.MissingChunk;
import com.pixurvival.core.message.PlayerActionRequest;
import com.pixurvival.core.message.RequestContentPacks;

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
	private @Getter ContentPacksContext contentPacksContext = new ContentPacksContext("contentPacks");
	private ContentPack localGamePack;
	private @Getter PlayerInventory myInventory;
	// private double timeRequestFrequencyMillis = 200;
	// private double timeRequestTimer = 0;

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
		Entity e = world.getEntityPool().get(EntityGroup.PLAYER, myPlayerId);
		if (e != null) {
			return (PlayerEntity) e;
		} else {
			return null;
		}
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

	public void setInitGame(InitializeGame initGame) {
		initializeGame(initGame);
		myInventory = initGame.getInventory();
	}

	public void startLocalGame() {
		ContentPackIdentifier id = new ContentPackIdentifier("Vanilla", new Version("0.1"),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b"));
		try {
			localGamePack = contentPacksContext.load(id);
			World world = World.createLocalWorld(localGamePack);
			this.world = world;
			PlayerEntity playerEntity = new PlayerEntity();
			// TODO
			playerEntity.getPosition().set(0, 0);
			world.getEntityPool().add(playerEntity);
			Random random = new Random();
			for (int i = 0; i < 20; i++) {
				playerEntity.getInventory().setSlot(i,
						new ItemStack(
								localGamePack.getItemsById().get(random.nextInt(localGamePack.getItemsById().size())),
								random.nextInt(10) + 1));
			}
			myPlayerId = playerEntity.getId();
			myInventory = playerEntity.getInventory();

			ItemStackEntity itemStackEntity = new ItemStackEntity(new ItemStack(localGamePack.getItems().get("apple")));
			itemStackEntity.getPosition().set(10, 10);
			world.getEntityPool().add(itemStackEntity);

			notify(l -> l.initializeGame());
		} catch (ContentPackException e) {
			e.printStackTrace();
		}
	}

	public void sendAction(PlayerActionRequest request) {
		if (world != null) {
			if (world.getType() == World.Type.CLIENT) {
				client.sendUDP(request);
				PlayerEntity e = getMyPlayer();
				if (e != null) {
					// ApplyPlayerActionAction action = new
					// ApplyPlayerActionAction((PlayerEntity) e, request);
					// world.getActionTimerManager().add(new ActionTimer(action,
					// world.getTime().getTimeMillis() +
					// world.getTime().getTimeOffsetMillis()));
					e.apply(request);
				}
			} else {
				getMyPlayer().apply(request);
			}
		}
	}

	public void sendAction(InventoryActionRequest request) {
		if (world != null) {
			if (world.getType() == World.Type.CLIENT) {
				client.sendUDP(request);
				if (getMyPlayer() != null) {
					getMyPlayer().apply(request);
				}
			} else {
				getMyPlayer().apply(request);
			}
		}
	}

	public void sendAction(InteractStructureRequest request) {
		if (world != null) {
			if (world.getType() == World.Type.CLIENT) {
				client.sendUDP(request);
			} else {
				getMyPlayer().apply(request);
			}
		}
	}

	public void sendAction(CraftItemRequest request) {
		if (world != null) {
			if (world.getType() == World.Type.CLIENT) {
				client.sendUDP(request);
			} else {
				getMyPlayer().apply(request);
			}
		}
	}

	public void sendAction(DropItemRequest request) {
		if (world != null) {
			if (world.getType() == World.Type.CLIENT) {
				client.sendUDP(request);
			} else {
				getMyPlayer().apply(request);
			}
		}
	}

	public void update(double deltaTimeMillis) {
		clientListener.consumeReceivedObjects();
		if (world != null) {
			if (getMyPlayer() != null) {
				getMyPlayer().setInventory(myInventory);
			}
			world.update(deltaTimeMillis);
			// timeRequestTimer += deltaTimeMillis;
			// if (timeRequestTimer >= timeRequestFrequencyMillis) {
			// timeRequestTimer -= timeRequestFrequencyMillis;
			// client.sendUDP(new TimeRequest(System.currentTimeMillis()));
			// }
			if (world.getType() == World.Type.CLIENT) {
				Position[] missingChunks = world.getMap().pollMissingChunks();
				if (missingChunks != null) {
					client.sendUDP(new MissingChunk(missingChunks));
				}
			}
		}
	}

	public void checkMissingPacks(ContentPackIdentifier[] identifiers) {
		Collection<ContentPackIdentifier> list = contentPacksContext.list();
		List<ContentPackIdentifier> missingPacks = new ArrayList<>();
		for (ContentPackIdentifier identifier : identifiers) {
			if (!list.contains(identifier)) {
				missingPacks.add(identifier);
			}
		}
		if (!missingPacks.isEmpty()) {
			client.sendTCP(
					new RequestContentPacks(missingPacks.toArray(new ContentPackIdentifier[missingPacks.size()])));
		}
		contentPackDownloadManager.setMissingList(missingPacks);
	}

	public void updatePing(long timeMillis) {
		if (world != null) {
			world.getTime().updateOffset(timeMillis);
		}
	}

	public void notifyReady() {
		client.sendTCP(new GameReady());
	}

	private void initializeGame(InitializeGame initGame) {
		try {
			setWorld(World.createClientWorld(initGame.getCreateWorld(), getContentPacksContext()));
			myPlayerId = initGame.getMyPlayerId();
			myInventory = initGame.getInventory();
			notify(l -> l.initializeGame());
		} catch (ContentPackException e) {
			Log.error("Error occured when loading contentPack.", e);
			notify(l -> l.error(e));
		}
		initGame = null;
	}
}
