package com.pixurvival.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.pixurvival.core.PlayerEntity;
import com.pixurvival.core.World;
import com.pixurvival.core.map.Tile;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.KryoInitializer;
import com.pixurvival.core.message.StartGame;

public class ServerGame {

	private KryoServer server = new KryoServer();
	private ServerListener serverListener = new ServerListener(this);
	private List<ServerGameListener> listeners = new ArrayList<>();
	private ServerEngineThread thread = new ServerEngineThread(this);

	public ServerGame() {
		server.addListener(serverListener);
		KryoInitializer.apply(server.getKryo());
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
		TiledMap tiledMap = new TiledMap(500, 500);
		tiledMap.getTiles().setAll(Tile.GRASS);
		World world = World.createServerWorld(tiledMap);
		CreateWorld createWorld = new CreateWorld();
		createWorld.setId(world.getId());
		createWorld.setMapWidth(500);
		createWorld.setMapHeight(500);
		foreachPlayers(playerConnection -> {
			PlayerEntity playerEntity = new PlayerEntity();
			playerEntity.getPosition().set(250, 250);
			world.getEntityPool().add(playerEntity);
			playerConnection.setPlayerEntity(playerEntity);
			playerConnection.sendTCP(new StartGame(createWorld, playerEntity.getId()));
		});
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
