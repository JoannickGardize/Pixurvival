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
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.WorldListener;
import com.pixurvival.core.contentPack.ContentPackContext;
import com.pixurvival.core.message.KryoInitializer;
import com.pixurvival.core.message.WorldKryo;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.ReleaseVersion;
import com.pixurvival.server.lobby.LobbySession;
import com.pixurvival.server.util.ServerMainArgs;

import lombok.Getter;
import lombok.SneakyThrows;

public class PixurvivalServer implements WorldListener {

	private Server server;
	private NetworkMessageHandler serverListener = new NetworkMessageHandler(this);
	private List<ServerGameListener> listeners = new ArrayList<>();

	private ServerEngineThread engineThread = new ServerEngineThread(this);
	private @Getter ContentPackContext contentPackContext;
	private @Getter ContentPackUploader contentPackUploader = new ContentPackUploader(this);
	private Map<String, PlayerConnection> connectionsByName = new HashMap<>();
	private List<LobbySession> lobbySessions = new ArrayList<>();
	private String[] gameBeginningCommands;

	@SneakyThrows
	public PixurvivalServer(ServerMainArgs serverArgs, ServerGameListener... listeners) {
		Log.info("Starting server version " + ReleaseVersion.actual().displayName());
		for (ServerGameListener listener : listeners) {
			this.listeners.add(listener);
		}
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
		contentPackContext = new ContentPackContext(defaultContentPack);
		addLobbySession(new LobbySession(this));
		gameBeginningCommands = serverArgs.getGameBeginingCommands();
		startServer(serverArgs.getPort());
	}

	public void sendCommand(String command) {
		engineThread.requestCommand(command);
	}

	public void runGame(GameSession session) {
		((WorldKryo) server.getKryo()).setWorld(session.getWorld());
		engineThread.add(session);
		for (String command : gameBeginningCommands) {
			engineThread.requestCommand(command);
		}
		listeners.forEach(l -> l.gameStarted(session));
		session.getWorld().addListener(this);
	}

	public void addLobbySession(LobbySession lobby) {
		lobbySessions.add(lobby);
		listeners.forEach(l -> l.lobbyStarted(lobby));
	}

	public void removeLobbySession(LobbySession lobby) {
		lobbySessions.remove(lobby);
	}

	public void addPlayerConnection(PlayerConnection playerConnection) {
		connectionsByName.put(playerConnection.toString(), playerConnection);
	}

	public int countPlayerConnection() {
		return connectionsByName.size();
	}

	public void removePlayerConnection(String name) {
		listeners.forEach(l -> l.disconnected(connectionsByName.remove(name)));
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
	}

	void playerLoggedIn(PlayerConnection playerConnection) {
		if (lobbySessions.size() == 1) {
			LobbySession gameLobby = lobbySessions.get(0);
			gameLobby.addPlayer(playerConnection);
			listeners.forEach(l -> l.playerLoggedIn(playerConnection));
		} else {
			listeners.forEach(l -> l.playerRejoined(playerConnection));
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

	@Override
	public void gameEnded(EndGameData data) {
		listeners.forEach(l -> l.gameEnded(data));
	}
}
