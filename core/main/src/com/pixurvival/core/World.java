package com.pixurvival.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

	private World(long id, Type type, TiledMap map) {
		this.id = id;
		this.type = type;
		this.map = map;
		entitiesUpdate.setWorldId(id);
	}

	public static World getWorld(long id) {
		return worlds.get(id);
	}

	public static World createClientWorld(CreateWorld createWorld) {
		TiledMap map = new TiledMap(createWorld.getMapHeight(), createWorld.getMapWidth());
		World world = new World(createWorld.getId(), Type.CLIENT, map);
		worlds.put(world.getId(), world);
		return world;
	}

	public static World createServerWorld(TiledMap map) {
		World world = new World(nextId++, Type.SERVER, map);
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
