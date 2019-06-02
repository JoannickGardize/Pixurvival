package com.pixurvival.core;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.chat.ChatManager;
import com.pixurvival.core.chat.ChatSender;
import com.pixurvival.core.command.CommandManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackLoader;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.entity.EntityPool;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.Team;
import com.pixurvival.core.livingEntity.TeamSet;
import com.pixurvival.core.map.ChunkManager;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.analytics.AreaSearchCriteria;
import com.pixurvival.core.map.analytics.CardinalDirection;
import com.pixurvival.core.map.analytics.GameAreaConfiguration;
import com.pixurvival.core.map.analytics.MapAnalytics;
import com.pixurvival.core.map.analytics.MapAnalyticsException;
import com.pixurvival.core.map.generator.ChunkSupplier;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.util.PluginHolder;
import com.pixurvival.core.util.Rectangle;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.core.util.WorldRandom;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class World extends PluginHolder<World> implements ChatSender {

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
	private ContentPack contentPack;
	private GameMode gameMode;
	private ChunkSupplier chunkSupplier;
	private File saveDirectory;
	private @Setter long myPlayerId = -1;
	private @Setter Object endGameConditionData;
	private TeamSet teamSet = new TeamSet();
	private @Getter CommandManager commandManager = new CommandManager();
	private @Getter ChatManager chatManager = new ChatManager();

	private World(long id, Type type, ContentPack contentPack, int gameModeId) {
		if (gameModeId < 0 || gameModeId >= contentPack.getGameModes().size()) {
			throw new IllegalArgumentException("No game mode with id " + gameModeId);
		}
		this.id = id;
		this.type = type;
		this.contentPack = contentPack;
		contentPack.initialize();
		this.gameMode = contentPack.getGameModes().get(gameModeId);
		map = new TiledMap(this);
		chunkSupplier = new ChunkSupplier(this, gameMode.getMapGenerator(), random.nextLong());
		// TODO make the world persistence great again
		// saveDirectory = new File(GlobalSettings.getSaveDirectory(),
		// uid.toString());
		// FileUtils.delete(saveDirectory);
		// saveDirectory.mkdirs();
	}

	public static World getWorld(long id) {
		return worlds.get(id);
	}

	public static World createClientWorld(CreateWorld createWorld, ContentPackLoader loader) throws ContentPackException {
		ContentPack pack = loader.load(createWorld.getContentPackIdentifier());
		World.currentContentPack = pack;
		World world = new World(createWorld.getId(), Type.CLIENT, pack, createWorld.getGameModeId());
		world.addPlugin(new WorldUpdateManager());
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

	public synchronized void update(double deltaTimeMillis) {
		updatePlugins(this);
		time.update(deltaTimeMillis);
		actionTimerManager.update();
		entityPool.update();
		map.update();
	}

	public void unload() {
		synchronized (map) {
			ChunkManager.getInstance().stopManaging(map);
			// FileUtils.delete(saveDirectory);
		}
	}

	public PlayerEntity getMyPlayer() {
		return (PlayerEntity) entityPool.get(EntityGroup.PLAYER, myPlayerId);
	}

	/**
	 * Called after all players are added in the EntityPool and Teams are sets.
	 * This will place players and set the map limit if present.
	 */
	public void initializeGame() {
		AreaSearchCriteria areaSearchCriteria = new AreaSearchCriteria();
		areaSearchCriteria.setNumberOfSpawnSpots(teamSet.size());
		areaSearchCriteria.setSquareSize((int) gameMode.getSpawnSquareSize());
		MapAnalytics mapAnalytics = new MapAnalytics(random);
		try {
			GameAreaConfiguration config = mapAnalytics.buildGameAreaConfiguration(map, areaSearchCriteria);
			for (int i = 0; i < teamSet.size(); i++) {
				Team team = teamSet.get(i);
				Vector2 spawnPosition = config.getSpawnSpots()[i];
				CardinalDirection currentDirection = CardinalDirection.EAST;
				for (PlayerEntity player : team) {
					player.getPosition().set(spawnPosition);
					for (int j = 0; j < 4; j++) {
						if (map.tileAt((int) spawnPosition.getX() + currentDirection.getNormalX(), (int) spawnPosition.getY() + currentDirection.getNormalY()).isSolid()) {
							currentDirection = currentDirection.getNext();
						} else {
							spawnPosition.addX(currentDirection.getNormalX());
							spawnPosition.addY(currentDirection.getNormalY());
							break;
						}
					}
				}
			}
			if (gameMode.isMapLimitEnabled()) {
				Vector2 center = config.getArea().center();
				addPlugin(new MapLimitsManager(new Rectangle(center, gameMode.getMapLimitSize()), gameMode.getMapLimitDamagePerSecond()));
			}
		} catch (MapAnalyticsException e) {
			Log.error("MapAnalyticsException");
		}
	}

	@Override
	public String getName() {
		return "World";
	}
}
