package fr.sharkhendrix.pixurvival.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import fr.sharkhendrix.pixurvival.core.map.TiledMap;
import fr.sharkhendrix.pixurvival.core.network.message.CreateWorld;
import fr.sharkhendrix.pixurvival.core.network.message.EntitiesUpdate;
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
	private Time time;
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

	public boolean isClient() {
		return type.isClient();
	}

	public boolean isServer() {
		return type.isServer();
	}

	public synchronized void update(double deltaTimeMillis) {
		if (entitiesUpdate.getUpdateId() > previousUpdateId) {
			applyUpdate(entitiesUpdate.getInput());
		}
		time.update(deltaTimeMillis);
		entityPool.update();
		actionTimerManager.update();
	}

	public void writeUpdate(Output output) {
		entityPool.writeUpdate(output);
	}

	public void applyUpdate(Input input) {
		entityPool.applyUpdate(input);
	}
}
