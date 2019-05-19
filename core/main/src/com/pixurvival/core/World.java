package com.pixurvival.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackLoader;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.entity.EntityPool;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.TeamSet;
import com.pixurvival.core.map.ChunkManager;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.generator.ChunkSupplier;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.PlayerData;
import com.pixurvival.core.util.FileUtils;
import com.pixurvival.core.util.WorldRandom;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of = "id")
public class World {

	@Getter
	@AllArgsConstructor
	public enum Type {
		LOCAL(true, true),
		CLIENT(true, false),
		SERVER(false, true);

		private boolean client;
		private boolean server;
	}

	private static long nextId = 0;
	private static Map<Long, World> worlds = new HashMap<>();
	private static @Getter ContentPack currentContentPack;
	private Type type;
	private Time time = new Time();
	private TiledMap map;
	private EntityPool entityPool = new EntityPool(this);
	private WorldRandom random = new WorldRandom();
	private ActionTimerManager actionTimerManager = new ActionTimerManager(this);
	private long id;
	private UUID uid;
	private ContentPack contentPack;
	private GameMode gameMode;
	private ChunkSupplier chunkSupplier;
	private File saveDirectory;
	private List<PlayerData> playerDataList = new ArrayList<>();
	private SyncWorldUpdateManager syncWorldUpdateManager = new SyncWorldUpdateManager(this);
	private @Setter long myPlayerId = -1;
	private @Setter Object endGameConditionData;
	private TeamSet teamSet = new TeamSet();

	private World(long id, Type type, ContentPack contentPack, int gameModeId) {
		this.id = id;
		this.type = type;
		this.contentPack = contentPack;
		contentPack.initialize();
		this.gameMode = contentPack.getGameModes().get(gameModeId);
		map = new TiledMap(this);
		chunkSupplier = new ChunkSupplier(this, gameMode.getMapGenerator(), random.nextLong());
		uid = UUID.randomUUID();
		saveDirectory = new File(GlobalSettings.getSaveDirectory(), uid.toString());
		FileUtils.delete(saveDirectory);
		saveDirectory.mkdirs();
	}

	public static World getWorld(long id) {
		return worlds.get(id);
	}

	public static World createClientWorld(CreateWorld createWorld, ContentPackLoader loader) throws ContentPackException {
		ContentPack pack = loader.load(createWorld.getContentPackIdentifier());
		World.currentContentPack = pack;
		World world = new World(createWorld.getId(), Type.CLIENT, pack, createWorld.getGameModeId());
		worlds.put(world.getId(), world);
		return world;
	}

	public static World createServerWorld(ContentPack contentPack, int gameModeId) {
		World world = new World(nextId++, Type.SERVER, contentPack, gameModeId);
		worlds.put(world.getId(), world);
		return world;
	}

	public static World createLocalWorld(ContentPack contentPack, int gameModeId) {
		World world = new World(nextId++, Type.LOCAL, contentPack, gameModeId);
		worlds.put(world.getId(), world);
		return world;
	}

	public static Collection<World> getWorlds() {
		return worlds.values();
	}

	public boolean isClient() {
		return type.isClient();
	}

	public boolean isServer() {
		return type.isServer();
	}

	public void addPlayerData(Collection<PlayerData> playerData) {
		playerDataList.addAll(playerData);
	}

	public synchronized void update(double deltaTimeMillis) {
		if (!type.isServer()) {
			syncWorldUpdateManager.update();
			if (!playerDataList.isEmpty()) {
				for (int i = 0; i < playerDataList.size(); i++) {
					PlayerData playerData = playerDataList.get(i);
					PlayerEntity e = (PlayerEntity) entityPool.get(EntityGroup.PLAYER, playerData.getId());
					if (e != null) {
						e.applyData(playerData);
						playerDataList.remove(i);
						i--;
					}
				}
			}
		}
		time.update(deltaTimeMillis);
		actionTimerManager.update();
		entityPool.update();
		map.update();
	}

	public void unload() {
		synchronized (map) {
			ChunkManager.getInstance().stopManaging(map);
			FileUtils.delete(saveDirectory);
		}
	}

	public PlayerEntity getMyPlayer() {
		return (PlayerEntity) entityPool.get(EntityGroup.PLAYER, myPlayerId);
	}
}
