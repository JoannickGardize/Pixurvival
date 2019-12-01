package com.pixurvival.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.chat.ChatManager;
import com.pixurvival.core.chat.ChatSender;
import com.pixurvival.core.command.CommandManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.gameMode.event.Event;
import com.pixurvival.core.contentPack.gameMode.event.EventAction;
import com.pixurvival.core.contentPack.serialization.ContentPackSerializer;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.entity.EntityPool;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.analytics.AreaSearchCriteria;
import com.pixurvival.core.map.analytics.CardinalDirection;
import com.pixurvival.core.map.analytics.GameAreaConfiguration;
import com.pixurvival.core.map.analytics.MapAnalytics;
import com.pixurvival.core.map.analytics.MapAnalyticsException;
import com.pixurvival.core.map.chunk.ChunkManager;
import com.pixurvival.core.map.generator.ChunkSupplier;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.PlayerInformation;
import com.pixurvival.core.message.TeamComposition;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamSet;
import com.pixurvival.core.time.Time;
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
	private Time time;
	private TiledMap map;
	private EntityPool entityPool = new EntityPool(this);
	private WorldRandom random = new WorldRandom();
	private ActionTimerManager actionTimerManager = new ActionTimerManager(this);
	private long id;
	private ContentPack contentPack;
	private GameMode gameMode;
	private ChunkSupplier chunkSupplier;
	private @Setter Object endGameConditionData;
	private TeamSet teamSet = new TeamSet();
	private @Getter CommandManager commandManager = new CommandManager();
	private @Getter ChatManager chatManager = new ChatManager();
	private @Setter PlayerEntity myPlayer;
	private @Getter Vector2 spawnCenter;
	private @Getter Map<Long, PlayerEntity> playerEntities = new HashMap<>();
	private List<WorldListener> listeners = new ArrayList<>();
	private boolean gameEnded = false;

	private World(long id, Type type, ContentPack contentPack, int gameModeId) {
		if (gameModeId < 0 || gameModeId >= contentPack.getGameModes().size()) {
			throw new IllegalArgumentException("No game mode with id " + gameModeId);
		}
		this.id = id;
		this.type = type;
		this.contentPack = contentPack;
		contentPack.initialize();
		this.gameMode = contentPack.getGameModes().get(gameModeId);
		time = new Time(gameMode.getDayCycle().create());
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

	public static World createClientWorld(CreateWorld createWorld, ContentPackSerializer loader) throws ContentPackException {
		ContentPack pack = loader.load(createWorld.getContentPackIdentifier());
		World.currentContentPack = pack;
		World world = new World(createWorld.getId(), Type.CLIENT, pack, createWorld.getGameModeId());
		for (TeamComposition teamComposition : createWorld.getTeamCompositions()) {
			Team team = world.teamSet.createTeam(teamComposition.getTeamName());
			for (PlayerInformation playerInformation : teamComposition.getMembers()) {
				PlayerEntity player = new PlayerEntity();
				player.setId(playerInformation.getId());
				player.setName(playerInformation.getName());
				player.setTeam(team);
				player.setWorld(world);
				player.initialize();
				world.playerEntities.put(player.getId(), player);
			}
		}
		PlayerEntity myPlayer = world.playerEntities.get(createWorld.getMyPlayerId());
		myPlayer.getPosition().set(createWorld.getMyPosition());
		myPlayer.setInventory(createWorld.getInventory());
		world.myPlayer = myPlayer;
		world.getEntityPool().add(myPlayer);
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
		PlayerEntity playerEntity = new PlayerEntity();
		playerEntity.setTeam(world.getTeamSet().createTeam("Solo"));
		world.getEntityPool().add(playerEntity);
		world.myPlayer = playerEntity;
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

	public void addListener(WorldListener listener) {
		listeners.add(listener);
	}

	public synchronized void update(float deltaTimeMillis) {
		if (gameEnded) {
			return;
		}
		updatePlugins(this);
		time.update(deltaTimeMillis);
		actionTimerManager.update();
		entityPool.update();
		map.update();
		if (isServer() && gameMode.getEndGameCondition().update(this)) {
			long[] remainingPlayerIdArray = getEntityPool().get(EntityGroup.PLAYER).stream().filter(Entity::isAlive)
					.sorted((p1, p2) -> ((PlayerEntity) p1).getTeam().getName().compareTo(((PlayerEntity) p2).getTeam().getName())).mapToLong(Entity::getId).toArray();
			EndGameData endGameData = new EndGameData(time.getTimeMillis(), remainingPlayerIdArray);
			listeners.forEach(l -> l.gameEnded(endGameData));
			gameEnded = true;
		}
	}

	public void unload() {
		synchronized (map) {
			ChunkManager.getInstance().stopManaging(map);
			// FileUtils.delete(saveDirectory);
		}
	}

	/**
	 * Called after all players are added in the EntityPool and Teams are sets.
	 * This will place players and set the map limit if present.
	 */
	public void initializeGame() {
		entityPool.flushNewEntities();
		initializeSpawns();
		initializeEvents();
		gameMode.getEndGameCondition().initialize(this);
	}

	private void initializeSpawns() {
		AreaSearchCriteria areaSearchCriteria = new AreaSearchCriteria();
		areaSearchCriteria.setNumberOfSpawnSpots(teamSet.size());
		areaSearchCriteria.setSquareSize((int) gameMode.getSpawnSquareSize());
		MapAnalytics mapAnalytics = new MapAnalytics(random);
		try {
			GameAreaConfiguration config = mapAnalytics.buildGameAreaConfiguration(map, areaSearchCriteria);
			spawnCenter = config.getArea().center();
			for (int i = 0; i < teamSet.size(); i++) {
				Team team = teamSet.get(i);
				Vector2 spawnPosition = config.getSpawnSpots()[i];
				spawnTeam(team, spawnPosition);
			}
			if (gameMode.isMapLimitEnabled()) {
				addPlugin(new MapLimitsManager(new Rectangle(spawnCenter, gameMode.getMapLimitSize()), gameMode.getMapLimitDamagePerSecond()));
			}
		} catch (MapAnalyticsException e) {
			Log.error("MapAnalyticsException");
		}
	}

	private void initializeEvents() {
		for (Event event : gameMode.getEvents()) {
			actionTimerManager.addActionTimer(new EventAction(this, event), event.getStartTime());
		}
	}

	private void spawnTeam(Team team, Vector2 spawnPosition) {
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

	@Override
	public String getName() {
		return "World";
	}
}
