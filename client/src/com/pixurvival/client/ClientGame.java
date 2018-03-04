package com.pixurvival.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.PlayerEntity;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.ContentPacksContext;
import com.pixurvival.core.contentPack.Version;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.generator.MapBuilder;
import com.pixurvival.core.message.GameReady;
import com.pixurvival.core.message.InitializeGame;
import com.pixurvival.core.message.KryoInitializer;
import com.pixurvival.core.message.LoginRequest;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.MapPart;
import com.pixurvival.core.message.PlayerActionRequest;
import com.pixurvival.core.message.RequestContentPacks;
import com.pixurvival.core.util.ByteArray2D;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ClientGame {

	private Client client = new Client(8192, 8192);
	private ClientListener clientListener;
	private List<ClientGameListener> listeners = new ArrayList<>();
	private @Getter @Setter(AccessLevel.PACKAGE) World world = null;
	private @Getter @Setter(AccessLevel.PACKAGE) long myPlayerId;
	private @Getter ContentPackDownloadManager contentPackDownloadManager = new ContentPackDownloadManager();
	private @Getter ContentPacksContext contentPacksContext = new ContentPacksContext("contentPacks");
	private InitializeGame initGame;
	private int mapPartCount;
	private List<MapPart> mapParts = new ArrayList<>();
	private ByteArray2D buildingMap;
	private ContentPack localGamePack;
	// private double timeRequestFrequencyMillis = 200;
	// private double timeRequestTimer = 0;

	public ClientGame() {
		client = new Client(8192, 8192);
		KryoInitializer.apply(client.getKryo());
		clientListener = new ClientListener(this);
		// TODO enlever lag simulation
		client.addListener(new Listener.LagListener(100, 150, clientListener));
	}

	public void addListener(ClientGameListener listener) {
		listeners.add(listener);
	}

	void notify(Consumer<ClientGameListener> action) {
		listeners.forEach(action);
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
		this.initGame = initGame;
		mapPartCount = 0;
		buildingMap = new ByteArray2D(initGame.getCreateWorld().getMapWidth(),
				initGame.getCreateWorld().getMapHeight());
		buildingMap.fill((byte) 1);
	}

	public void startLocalGame() {
		ContentPackIdentifier id = new ContentPackIdentifier("Vanilla", new Version("0.1"),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b"));
		try {
			localGamePack = contentPacksContext.load(id);
			MapBuilder mapGenerator = new MapBuilder(localGamePack.getMapGenerator(), localGamePack.getTilesById());
			TiledMap tiledMap = mapGenerator.generate();
			World world = World.createLocalWorld(localGamePack, tiledMap);
			this.world = world;
			PlayerEntity playerEntity = new PlayerEntity();
			playerEntity.getPosition().set(tiledMap.getData().getWidth() / 2, tiledMap.getData().getHeight() / 2);
			world.getEntityPool().add(playerEntity);
			myPlayerId = playerEntity.getId();
			notify(l -> l.initializeGame());
		} catch (ContentPackException e) {
			e.printStackTrace();
		}
	}

	public void sendAction(PlayerActionRequest request) {
		if (world != null) {
			if (world.getType() == World.Type.CLIENT) {
				client.sendUDP(request);
				Entity e = world.getEntityPool().get(EntityGroup.PLAYER, myPlayerId);
				if (e != null) {
					// ApplyPlayerActionAction action = new
					// ApplyPlayerActionAction((PlayerEntity) e, request);
					// world.getActionTimerManager().add(new ActionTimer(action,
					// world.getTime().getTimeMillis() +
					// world.getTime().getTimeOffsetMillis()));
					((PlayerEntity) e).apply(request);
				}
			} else {
				((PlayerEntity) world.getEntityPool().get(EntityGroup.PLAYER, myPlayerId)).apply(request);
			}
		}
	}

	public void update(double deltaTimeMillis) {
		clientListener.consumeReceivedObjects();
		if (world != null) {
			world.update(deltaTimeMillis);
			// timeRequestTimer += deltaTimeMillis;
			// if (timeRequestTimer >= timeRequestFrequencyMillis) {
			// timeRequestTimer -= timeRequestFrequencyMillis;
			// client.sendUDP(new TimeRequest(System.currentTimeMillis()));
			// }
		}
		if (initGame != null) {
			if (buildingMap != null && !mapParts.isEmpty()) {
				mapParts.forEach(p -> buildingMap.setRect(p.getX(), p.getY(), p.getData()));
				mapPartCount += mapParts.size();
				mapParts.clear();
			}
			if (contentPackDownloadManager.isReady() && mapPartCount >= initGame.getCreateWorld().getPartCount()) {
				initializeGame();
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

	public void acceptMapPart(MapPart part) {
		mapParts.add(part);
	}

	public void updatePing(long timeMillis) {
		if (world != null) {
			world.getTime().updateOffset(timeMillis);
		}
	}

	public void notifyReady() {
		client.sendTCP(new GameReady());
	}

	private void initializeGame() {
		try {
			setWorld(World.createClientWorld(initGame.getCreateWorld(), getContentPacksContext(), buildingMap));
			setMyPlayerId(initGame.getMyPlayerId());
			notify(l -> l.initializeGame());
		} catch (ContentPackException e) {
			Log.error("Error occured when loading contentPack.", e);
			notify(l -> l.error(e));
		}
		initGame = null;
	}
}
