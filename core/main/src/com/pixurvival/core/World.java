package com.pixurvival.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPacksContext;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.generator.ChunkSupplier;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.EntitiesUpdate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "id")
public class World {

	public static final int PIXEL_PER_UNIT = 8;
	public static final double PIXEL_SIZE = 1.0 / PIXEL_PER_UNIT;
	public static final double PLAYER_VIEW_DISTANCE = 45;
	public static final int PLAYER_CHUNK_VIEW_DISTANCE = (int) Math.ceil(PLAYER_VIEW_DISTANCE / Chunk.CHUNK_SIZE);

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
	private long previousUpdateId = -1;
	private EntitiesUpdate entitiesUpdate = new EntitiesUpdate();
	private ContentPack contentPack;
	private ChunkSupplier chunkSupplier;

	private World(long id, Type type, ContentPack contentPack) {
		this.id = id;
		this.type = type;
		this.contentPack = contentPack;
		entitiesUpdate.setWorldId(id);
		map = new TiledMap(this);
		chunkSupplier = new ChunkSupplier(contentPack.getMapGenerator());
	}

	public static World getWorld(long id) {
		return worlds.get(id);
	}

	public static World createClientWorld(CreateWorld createWorld, ContentPacksContext contentPacksContext)
			throws ContentPackException {
		ContentPack pack = contentPacksContext.load(createWorld.getContentPackIdentifier());
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

	public void update(double deltaTimeMillis) {
		if (type != Type.SERVER && entitiesUpdate.getUpdateId() > previousUpdateId) {

			entityPool.applyUpdate(entitiesUpdate.getByteBuffer());
			previousUpdateId = entitiesUpdate.getUpdateId();
		}
		time.update(deltaTimeMillis);
		actionTimerManager.update();
		entityPool.update();
		map.update();
	}

	public void writeEntitiesUpdate() {
		entitiesUpdate.setUpdateId(++previousUpdateId);
		entityPool.writeUpdate(entitiesUpdate.getByteBuffer());
	}
}
