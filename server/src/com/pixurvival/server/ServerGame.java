package com.pixurvival.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.Version;
import com.pixurvival.core.contentPack.serialization.ContentPackSerializer;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.Team;
import com.pixurvival.core.map.ChunkManager;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.KryoInitializer;
import com.pixurvival.core.message.StartGame;
import com.pixurvival.core.util.CommonMainArgs;

import lombok.Getter;
import lombok.SneakyThrows;

public class ServerGame {

	private KryoServer server = new KryoServer();
	private NetworkMessageHandler serverListener = new NetworkMessageHandler(this);
	private List<ServerGameListener> listeners = new ArrayList<>();
	private ServerEngineThread engineThread = new ServerEngineThread(this);
	// private @Getter ContentPacksContext contentPacksContext = new
	// ContentPacksContext("contentPacks");
	private @Getter ContentPack selectedContentPack;
	private @Getter ContentPackUploadManager contentPackUploadManager = new ContentPackUploadManager(this);
	private Map<String, PlayerConnection> connectionsByName = new HashMap<>();

	@SneakyThrows
	public ServerGame(CommonMainArgs serverArgs) {
		serverArgs.apply(server, serverListener);
		contentPackUploadManager.start();
		addListener(contentPackUploadManager);
		KryoInitializer.apply(server.getKryo());
		// TODO selection dynamique des packs
		ContentPackIdentifier id = new ContentPackIdentifier("Vanilla", new Version("1.0"));

		File defaultContentPack;
		if (serverArgs.getContentPackDirectory() == null) {
			defaultContentPack = new File("contentPacks");
		} else {
			defaultContentPack = new File(serverArgs.getContentPackDirectory());
		}
		setSelectedContentPack(new ContentPackSerializer(defaultContentPack).load(id));
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
		engineThread.start();
	}

	public void stopServer() {
		server.stop();
		server.close();
		engineThread.setRunning(false);
		contentPackUploadManager.setRunning(false);
		ChunkManager.getInstance().setRunning(false);
		World.getWorlds().forEach(World::unload);
	}

	public void startGame(int gameModeId) {
		// TODO choix du contentPack
		World world = World.createServerWorld(selectedContentPack, gameModeId);
		GameSession session = new GameSession(world);
		CreateWorld createWorld = new CreateWorld();
		createWorld.setId(world.getId());
		createWorld.setContentPackIdentifier(new ContentPackIdentifier(selectedContentPack.getIdentifier()));
		createWorld.setGameModeId(gameModeId);
		foreachPlayers(playerConnection -> {
			Team team = world.getTeamSet().get(playerConnection.getRequestedTeamName());
			if (team == null) {
				team = world.getTeamSet().createTeam(playerConnection.getRequestedTeamName());
			}
		});
		createWorld.setTeamNames(world.getTeamSet().getNames());
		foreachPlayers(playerConnection -> {
			PlayerEntity playerEntity = new PlayerEntity();
			world.getEntityPool().add(playerEntity);
			playerEntity.setTeam(world.getTeamSet().get(playerConnection.getRequestedTeamName()));
			playerEntity.setName(playerConnection.toString());
			playerEntity.getInventory().addListener(playerConnection);
			playerEntity.getEquipment().addListener(playerConnection);
			playerConnection.setPlayerEntity(playerEntity);
			createWorld.setMyPlayerId(playerEntity.getId());
			createWorld.setMyTeamId(playerEntity.getTeam().getId());
			createWorld.setInventory(playerEntity.getInventory());
			playerConnection.sendTCP(createWorld);
			session.addPlayer(playerConnection);
		});
		world.initializeGame();

		foreachPlayers(playerConnection -> {
			boolean messageSent = false;
			while (!messageSent) {
				if (playerConnection.isWorldReady()) {
					messageSent = true;
				} else {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		foreachPlayers(playerConnection -> playerConnection.sendTCP(new StartGame()));
		engineThread.add(session);
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
