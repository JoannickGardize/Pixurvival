package com.pixurvival.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.PlayerEntity;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.ContentPacksContext;
import com.pixurvival.core.contentPack.Version;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.generator.MapBuilder;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.InitializeGame;
import com.pixurvival.core.message.KryoInitializer;
import com.pixurvival.core.message.MapPart;
import com.pixurvival.core.util.ByteArray2D;

import lombok.Getter;

public class ServerGame {

	private KryoServer server = new KryoServer();
	private ServerListener serverListener = new ServerListener(this);
	private List<ServerGameListener> listeners = new ArrayList<>();
	private ServerEngineThread thread = new ServerEngineThread(this);
	private @Getter ContentPacksContext contentPacksContext = new ContentPacksContext("contentPacks");
	private @Getter ContentPack selectedContentPack;
	private @Getter ContentPackUploadManager contentPackUploadManager = new ContentPackUploadManager(this);

	public ServerGame() {
		Log.set(Log.LEVEL_DEBUG);
		contentPackUploadManager.start();
		server.addListener(serverListener);
		addListener(contentPackUploadManager);
		KryoInitializer.apply(server.getKryo());
		// TODO selection dynamique des packs
		ContentPackIdentifier id = new ContentPackIdentifier("Vanilla", new Version("0.1"),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b"));
		try {
			setSelectedContentPack(contentPacksContext.load(id));
		} catch (ContentPackException e) {
			e.printStackTrace();
		}
	}

	public void setSelectedContentPack(ContentPack contentPack) {
		selectedContentPack = contentPack;
		contentPackUploadManager.setSelectedContentPack(contentPack);
	}

	public void addListener(ServerGameListener listener) {
		listeners.add(listener);
	}

	public void startServer(int port) throws IOException {
		server.start();
		server.bind(port, port);
		thread.start();
	}

	public void stopServer() {
		server.stop();
		server.close();
	}

	public void startTestGame() {
		MapBuilder mapGenerator = new MapBuilder(selectedContentPack.getMapGenerator(),
				selectedContentPack.getTilesById());
		TiledMap tiledMap = mapGenerator.generate();
		World world = World.createServerWorld(selectedContentPack, tiledMap);
		CreateWorld createWorld = new CreateWorld();
		createWorld.setId(world.getId());
		createWorld.setMapWidth(tiledMap.getData().getWidth());
		createWorld.setMapHeight(tiledMap.getData().getHeight());
		int partCountX = (int) Math.ceil(tiledMap.getData().getWidth() / 64);
		int partCountY = (int) Math.ceil(tiledMap.getData().getHeight() / 64);
		createWorld.setPartCount(partCountX * partCountY);
		createWorld.setContentPackIdentifier(new ContentPackIdentifier(selectedContentPack.getInfo()));
		foreachPlayers(playerConnection -> {
			PlayerEntity playerEntity = new PlayerEntity();
			playerEntity.getPosition().set(tiledMap.getData().getWidth() / 2, tiledMap.getData().getHeight() / 2);
			world.getEntityPool().add(playerEntity);
			playerConnection.setPlayerEntity(playerEntity);
			playerConnection.sendTCP(new InitializeGame(createWorld, playerEntity.getId()));
		});
		for (int x = 0; x < partCountX; x++) {
			for (int y = 0; y < partCountY; y++) {
				int width = x < partCountX - 1 ? 64 : tiledMap.getData().getWidth() - x * 64;
				int height = y < partCountY - 1 ? 64 : tiledMap.getData().getHeight() - y * 64;
				ByteArray2D data = tiledMap.getData().getRect(x * 64, y * 64, width, height);
				MapPart part = new MapPart(x * 64, y * 64, data);
				foreachPlayers(playerConnection -> {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					playerConnection.sendTCP(part);
				});
			}
		}
	}

	void notify(Consumer<ServerGameListener> action) {
		listeners.forEach(action);
	}

	void consumeReceivedObjects() {
		serverListener.consumeReceivedObjects();
	}

	public void foreachPlayers(Consumer<PlayerConnection> action) {
		for (Connection connection : server.getConnections()) {
			if (connection.isConnected()) {
				action.accept((PlayerConnection) connection);
			}
		}
	}
}
