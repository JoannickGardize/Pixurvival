package com.pixurvival.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackLoader;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.ChunkManager;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.generator.ChunkSupplier;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.PlayerData;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.util.FileUtils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "id")
public class World {

	@Getter
	@AllArgsConstructor
	public static enum Type {
		LOCAL(true, true),
		CLIENT(true, false),
		SERVER(false, true);

		private boolean client;
		private boolean server;
	}

	private static long nextId = 0;
	private static Map<Long, World> worlds = new HashMap<>();
	private @Getter static ContentPack currentContentPack;
	private Type type;
	private Time time = new Time();
	private TiledMap map;
	private EntityPool entityPool = new EntityPool(this);
	private Random random = new Random();
	private ActionTimerManager actionTimerManager = new ActionTimerManager(this);
	private long id;
	private UUID uid;
	private long previousUpdateId = -1;
	private WorldUpdate worldUpdate = new WorldUpdate();
	private ContentPack contentPack;
	private ChunkSupplier chunkSupplier;
	private File saveDirectory;
	private List<PlayerData> playerDataList = new ArrayList<>();
	private SyncWorldUpdateManager syncWorldUpdateManager = new SyncWorldUpdateManager(this);

	private World(long id, Type type, ContentPack contentPack) {
		this.id = id;
		this.type = type;
		this.contentPack = contentPack;
		worldUpdate.setWorldId(id);
		map = new TiledMap(this);
		// TODO Choix du mapGenerator en fonction du gameMode
		chunkSupplier = new ChunkSupplier(this, contentPack.getMapGenerators().get(0), new Random().nextLong());
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
		World world = new World(createWorld.getId(), Type.CLIENT, pack);
		worlds.put(world.getId(), world);
		return world;
	}

	public static World createServerWorld(ContentPack contentPack) {
		World world = new World(nextId++, Type.SERVER, contentPack);
		worlds.put(world.getId(), world);
		return world;
	}

	public static World createLocalWorld(ContentPack contentPack) {
		World world = new World(nextId++, Type.LOCAL, contentPack);
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

	public void addPlayerData(PlayerData[] playerData) {
		playerDataList.addAll(Arrays.asList(playerData));
	}

	public synchronized void update(double deltaTimeMillis) {
		WorldUpdate worldUpdate = this.worldUpdate;
		if (!type.isServer()) {
			syncWorldUpdateManager.update();
			if (worldUpdate.getUpdateId() > previousUpdateId) {
				entityPool.applyUpdate(worldUpdate.getByteBuffer());
				previousUpdateId = worldUpdate.getUpdateId();
			}
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

	public void incrementUpdateId() {
		worldUpdate.setUpdateId(++previousUpdateId);
	}

	public void writeWorldUpdateFor(PlayerEntity player) {
		entityPool.writeUpdate(player, worldUpdate.getByteBuffer());
	}

	public void unload() {
		synchronized (map) {
			ChunkManager.getInstance().stopManaging(map);
			FileUtils.delete(saveDirectory);
		}
	}
}
