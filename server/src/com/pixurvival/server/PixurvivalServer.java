package com.pixurvival.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.serialization.ContentPackSerialization;
import com.pixurvival.core.map.chunk.ChunkManager;
import com.pixurvival.core.message.KryoInitializer;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.server.lobby.LobbySession;
import com.pixurvival.server.util.ServerMainArgs;

import lombok.Getter;
import lombok.SneakyThrows;

public class PixurvivalServer {

	private Server server;
	private NetworkMessageHandler serverListener = new NetworkMessageHandler(this);
	private List<ServerGameListener> listeners = new ArrayList<>();

	private ServerEngineThread engineThread = new ServerEngineThread(this);
	private @Getter ContentPackSerialization contentPackSerialization;
	private @Getter ContentPackUploader contentPackUploader = new ContentPackUploader(this);
	private Map<String, PlayerConnection> connectionsByName = new HashMap<>();
	private List<LobbySession> lobbySessions = new ArrayList<>();

	@SneakyThrows
	public PixurvivalServer(ServerMainArgs serverArgs) {
		if (MathUtils.equals(serverArgs.getSimulatePacketLossRate(), 0)) {
			server = new KryoServer();
		} else {
			server = new SimulatePacketLostKryoServer(serverArgs.getSimulatePacketLossRate());
			Log.warn("Simulating UDP packet loss with a rate of " + serverArgs.getSimulatePacketLossRate());
		}
		serverArgs.apply(server, serverListener);
		contentPackUploader.start();
		KryoInitializer.apply(server.getKryo());
		File defaultContentPack = new File(serverArgs.getContentPackDirectory());
		contentPackSerialization = new ContentPackSerialization(defaultContentPack);
		addLobbySession(new LobbySession(this));
		startServer(serverArgs.getDefaultPort());
	}

	public void runGame(GameSession session) {
		engineThread.add(session);
	}

	public void addLobbySession(LobbySession lobby) {
		lobbySessions.add(lobby);
	}

	public void removeLobbySession(LobbySession lobby) {
		lobbySessions.remove(lobby);
	}

	public void addPlayerConnection(PlayerConnection playerConnection) {
		connectionsByName.put(playerConnection.toString(), playerConnection);
	}

	public void removePlayerConnection(String name) {
		connectionsByName.remove(name);
	}

	public PlayerConnection getPlayerConnection(String name) {
		return connectionsByName.get(name);
	}

	public void addListener(ServerGameListener listener) {
		listeners.add(listener);
	}

	public void startServer(int port) throws IOException {
		server.start();
		server.bind(port, port);
		engineThread.start();
	}

	public void stopServer() {
		server.stop();
		server.close();
		engineThread.setRunning(false);
		contentPackUploader.setRunning(false);
		ChunkManager.getInstance().setRunning(false);
		World.getWorlds().forEach(World::unload);
	}

	void playerLoggedIn(PlayerConnection playerConnection) {
		if (lobbySessions.size() == 1) {
			LobbySession gameLobby = lobbySessions.get(0);
			gameLobby.addPlayer(playerConnection);
		} else {
			listeners.forEach(l -> l.playerLoggedIn(playerConnection));
		}
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
