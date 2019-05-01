package com.pixurvival.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.ContentPackLoader;
import com.pixurvival.core.contentPack.Version;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.ChunkManager;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.InitializeGame;
import com.pixurvival.core.message.KryoInitializer;
import com.pixurvival.core.message.PlayerData;
import com.pixurvival.core.util.CommonMainArgs;

import lombok.Getter;
import lombok.SneakyThrows;

public class ServerGame {

	private KryoServer server = new KryoServer();
	private ServerListener serverListener = new ServerListener(this);
	private List<ServerGameListener> listeners = new ArrayList<>();
	private ServerEngineThread engineThread = new ServerEngineThread(this);
	// private @Getter ContentPacksContext contentPacksContext = new
	// ContentPacksContext("contentPacks");
	private @Getter ContentPack selectedContentPack;
	private @Getter ContentPackUploadManager contentPackUploadManager = new ContentPackUploadManager(this);

	@SneakyThrows
	public ServerGame(CommonMainArgs serverArgs) {
		serverArgs.apply(server, serverListener);
		contentPackUploadManager.start();
		addListener(contentPackUploadManager);
		KryoInitializer.apply(server.getKryo());
		// TODO selection dynamique des packs
		ContentPackIdentifier id = new ContentPackIdentifier("Vanilla", new Version("1.0"));

		try {
			setSelectedContentPack(new ContentPackLoader(new File("contentPacks")).load(id));
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

	public void startTestGame() {
		World world = World.createServerWorld(selectedContentPack);
		GameSession session = new GameSession(world);
		CreateWorld createWorld = new CreateWorld();
		createWorld.setId(world.getId());
		createWorld.setContentPackIdentifier(new ContentPackIdentifier(selectedContentPack.getIdentifier()));
		List<PlayerData> playerDataList = new ArrayList<>();
		foreachPlayers(playerConnection -> playerConnection.sendTCP(createWorld));
		foreachPlayers(playerConnection -> {
			PlayerEntity playerEntity = new PlayerEntity();
			playerEntity.setName(playerConnection.toString());
			Random random = new Random();
			world.getEntityPool().add(playerEntity);
			playerConnection.setPlayerEntity(playerEntity);
			playerEntity.getInventory().addListener(playerConnection);
			playerEntity.getEquipment().addListener(playerConnection);
			playerDataList.add(playerEntity.getData());
			session.addPlayer(playerConnection);
			for (int i = 0; i < 20; i++) {
				playerEntity.getInventory().setSlot(i, new ItemStack(selectedContentPack.getItems().get(random.nextInt(selectedContentPack.getItems().size())), random.nextInt(10) + 1));
			}
		});

		// TODO Retirer
		CreatureEntity creatureEntity = new CreatureEntity(selectedContentPack.getCreatures().get(0));
		world.getEntityPool().add(creatureEntity);

		PlayerData[] playerData = playerDataList.toArray(new PlayerData[playerDataList.size()]);
		foreachPlayers(playerConnection -> {
			boolean messageSent = false;
			while (!messageSent) {
				if (playerConnection.isWorldReady()) {
					messageSent = true;
					playerConnection.sendTCP(new InitializeGame(playerConnection.getPlayerEntity().getId(), playerConnection.getPlayerEntity().getInventory(), playerData));
				} else {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
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
