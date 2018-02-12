package com.pixurvival.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPacksContext;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.EntitiesUpdate;

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

	private World(long id, Type type, TiledMap map, ContentPack contentPack) {
		this.id = id;
		this.type = type;
		this.map = map;
		this.contentPack = contentPack;
		entitiesUpdate.setWorldId(id);
	}

	public static World getWorld(long id) {
		return worlds.get(id);
	}

	public static World createClientWorld(CreateWorld createWorld, ContentPacksContext contentPacksContext)
			throws ContentPackException {
		TiledMap map = new TiledMap(createWorld.getMapHeight(), createWorld.getMapWidth());
		World world = new World(createWorld.getId(), Type.CLIENT, map,
				contentPacksContext.load(createWorld.getContentPackIdentifier()));
		worlds.put(world.getId(), world);
		return world;
	}

	public static World createServerWorld(ContentPack contentPack, TiledMap map) {
		World world = new World(nextId++, Type.SERVER, map, contentPack);
		worlds.put(world.getId(), world);
		return world;
	}

	public static World createLocalWorld(ContentPack contentPack, TiledMap map) {
		World world = new World(nextId++, Type.LOCAL, map, contentPack);
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
		entityPool.update();
		actionTimerManager.update();
	}

	public void writeEntitiesUpdate() {
		entitiesUpdate.setUpdateId(++previousUpdateId);
		entityPool.writeUpdate(entitiesUpdate.getByteBuffer());
	}
}