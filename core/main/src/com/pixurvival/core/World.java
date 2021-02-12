package com.pixurvival.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.chat.ChatEntry;
import com.pixurvival.core.chat.ChatManager;
import com.pixurvival.core.chat.ChatSender;
import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.command.CommandManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackContext;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.gameMode.event.EventAction;
import com.pixurvival.core.contentPack.gameMode.role.Roles;
import com.pixurvival.core.entity.EntityPool;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.map.ChunkCreatureSpawnManager;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.analytics.MapAnalyticsException;
import com.pixurvival.core.map.chunk.ChunkManager;
import com.pixurvival.core.map.generator.ChunkSupplier;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.PlayerInformation;
import com.pixurvival.core.message.TeamComposition;
import com.pixurvival.core.message.WorldKryo;
import com.pixurvival.core.system.GameSystem;
import com.pixurvival.core.system.HungerSystem;
import com.pixurvival.core.system.InteractionDialogSystem;
import com.pixurvival.core.system.WorldAttributesAccessor;
import com.pixurvival.core.system.interest.InitializeNewClientWorldInterest;
import com.pixurvival.core.system.interest.InitializeNewServerWorldInterest;
import com.pixurvival.core.system.interest.InterestSubscription;
import com.pixurvival.core.system.interest.InterestSubscriptionSet;
import com.pixurvival.core.system.interest.PersistenceInterest;
import com.pixurvival.core.system.interest.WorldUpdateInterest;
import com.pixurvival.core.system.mapLimits.MapLimitsSystem;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamSet;
import com.pixurvival.core.time.Time;
import com.pixurvival.core.util.PluginHolder;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.core.util.WorldRandom;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class World extends PluginHolder<World> implements ChatSender, CommandExecutor {

	@Getter
	@AllArgsConstructor
	public enum Type {
		LOCAL(true, true),
		CLIENT(true, false),
		SERVER(false, true);

		private boolean client;
		private boolean server;
	}

	private final Type type;
	private InterestSubscriptionSet interestSubscriptionSet = new InterestSubscriptionSet();
	private Time time;
	private TiledMap map;
	private ChunkManager chunkManager;
	private EntityPool entityPool = new EntityPool(this);
	private WorldRandom random = new WorldRandom();
	private ActionTimerManager actionTimerManager = new ActionTimerManager(this);
	private long id;
	private ContentPack contentPack;
	private GameMode gameMode;
	private ChunkSupplier chunkSupplier;
	private @Setter Map<Integer, Object> endGameConditionData = new HashMap<>();
	private TeamSet teamSet = new TeamSet();
	private CommandManager commandManager = new CommandManager();
	private ChatManager chatManager = new ChatManager();
	private @Setter PlayerEntity myPlayer;
	private @Setter Vector2 spawnCenter;
	private Map<Long, PlayerEntity> playerEntities = new HashMap<>();
	private List<WorldListener> listeners = new ArrayList<>();
	private boolean gameEnded = false;
	private ChunkCreatureSpawnManager chunkCreatureSpawnManager = new ChunkCreatureSpawnManager();
	private @Setter String saveName;
	private long seed;
	private WorldKryo inventoryKryo;

	private List<AdditionalAttribute> additionalAttributes = new ArrayList<>();
	private Map<Class<? extends GameSystem>, GameSystem> systems = new LinkedHashMap<>();

	private InterestSubscription<InitializeNewServerWorldInterest> initializeNewServerWorldSubscription = interestSubscriptionSet.get(InitializeNewServerWorldInterest.class);
	private InterestSubscription<InitializeNewClientWorldInterest> initializeNewClientWorldSubscription = interestSubscriptionSet.get(InitializeNewClientWorldInterest.class);
	private InterestSubscription<WorldUpdateInterest> worldUpdateSubscription = interestSubscriptionSet.get(WorldUpdateInterest.class);
	private InterestSubscription<PersistenceInterest> persitenceSubscription = interestSubscriptionSet.get(PersistenceInterest.class);

	private World(long id, Type type, ContentPack contentPack, int gameModeId) {
		this(id, type, contentPack, gameModeId, new Random().nextLong());
	}

	private World(long id, Type type, ContentPack contentPack, int gameModeId, long seed) {
		if (gameModeId < 0 || gameModeId >= contentPack.getGameModes().size()) {
			throw new IllegalArgumentException("No game mode with id " + gameModeId);
		}
		this.id = id;
		this.type = type;
		this.contentPack = contentPack;
		this.gameMode = contentPack.getGameModes().get(gameModeId);
		this.seed = seed;
		time = new Time(gameMode.getDayCycle().create(), interestSubscriptionSet);
		map = new TiledMap(this);
		chunkSupplier = new ChunkSupplier(this);
		chunkManager = new ChunkManager(map);
		inventoryKryo = new WorldKryo();
		inventoryKryo.setWorld(this);
		inventoryKryo.setReferences(false);
		inventoryKryo.register(ItemStack.class, new ItemStack.Serializer());
		inventoryKryo.register(PlayerInventory.class, new PlayerInventory.Serializer());
		inventoryKryo.register(Inventory.class, new Inventory.Serializer());
		addSystem(HungerSystem.class);
		addSystem(MapLimitsSystem.class);
		addSystem(InteractionDialogSystem.class);
	}

	public void addAdditonalAttribute(Object o, Class<?> type, Class<?>... genericTypes) {
		additionalAttributes.add(new AdditionalAttribute(o, type, genericTypes));
	}

	@SneakyThrows
	public void addSystem(Class<? extends GameSystem> type) {
		systems.put(type, type.getConstructor().newInstance());
	}

	@SuppressWarnings("unchecked")
	public <T extends GameSystem> T getSystem(Class<? extends GameSystem> type) {
		return (T) systems.get(type);
	}

	private void initializeSystems() {
		systems.values().removeIf(s -> !s.isRequired(gameMode));
		WorldAttributesAccessor worldAttributesAccessor = new WorldAttributesAccessor(this);
		systems.values().forEach(worldAttributesAccessor::inject);
		systems.values().forEach(interestSubscriptionSet::subscribeAll);
	}

	public static World createClientWorld(CreateWorld createWorld, ContentPackContext contentPackContext) throws ContentPackException {
		ContentPack pack = contentPackContext.load(createWorld.getContentPackIdentifier());
		pack.initialize();
		World world = new World(createWorld.getId(), Type.CLIENT, pack, createWorld.getGameModeId());
		for (TeamComposition teamComposition : createWorld.getTeamCompositions()) {
			Team team = world.teamSet.createTeam(teamComposition.getTeamName());
			for (PlayerInformation playerInformation : teamComposition.getMembers()) {
				PlayerEntity player = new PlayerEntity();
				player.setId(playerInformation.getId());
				player.setName(playerInformation.getName());
				if (world.getGameMode().getRoles() != null && playerInformation.getRoleId() != -1) {
					player.setRole(world.getGameMode().getRoles().getRoles().get(playerInformation.getRoleId()));
				}
				player.setTeam(team);
				player.setWorld(world);
				player.initialize();
				world.playerEntities.put(player.getId(), player);
			}
		}
		PlayerEntity myPlayer = world.playerEntities.get(createWorld.getMyPlayerId());
		myPlayer.getPosition().set(createWorld.getMyPosition());
		myPlayer.setSpawnPosition(createWorld.getMySpawnCenter());

		createWorld.getInventory().computeQuantities();
		myPlayer.setInventory(createWorld.getInventory());
		world.myPlayer = myPlayer;
		world.getEntityPool().add(myPlayer);
		world.setSpawnCenter(createWorld.getWorldSpawnCenter());
		return world;
	}

	public static World createServerWorld(ContentPack contentPack, int gameModeId) {
		contentPack.initialize();
		World world = new World(0, Type.SERVER, contentPack, gameModeId);
		// TODO manage multiples worlds
		return world;
	}

	// createNewLocalWorld
	public static World createNewLocalWorld(ContentPack contentPack, int gameModeId) {
		contentPack.initialize();
		World world = new World(0, Type.LOCAL, contentPack, gameModeId);
		PlayerEntity playerEntity = new PlayerEntity();
		world.getEntityPool().addNew(playerEntity);
		playerEntity.setOperator(true);
		playerEntity.setTeam(world.getTeamSet().createTeam("Default"));
		world.myPlayer = playerEntity;
		world.playerEntities.put(playerEntity.getId(), playerEntity);
		return world;
	}

	// createExistingLocalWorld
	public static World createExistingLocalWorld(ContentPack contentPack, int gameModeId, long seed) {
		contentPack.initialize();
		World world = new World(0, Type.LOCAL, contentPack, gameModeId, seed);
		return world;
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
		worldUpdateSubscription.forEach(w -> w.update(time.getDeltaTime()));
		time.update(deltaTimeMillis);
		actionTimerManager.update();
		entityPool.update();
		map.update();
		if (isServer() && gameMode.updateEndGameConditions(this)) {
			List<PlayerEndGameData> wonPlayerIds = new ArrayList<>();
			List<PlayerEndGameData> lostPlayerIds = new ArrayList<>();
			for (PlayerEntity player : getPlayerEntities().values()) {
				PlayerEndGameData playerData = new PlayerEndGameData(player);
				if (player.getWinCondition().test(player)) {
					wonPlayerIds.add(playerData);
				} else {
					lostPlayerIds.add(playerData);
				}
			}
			EndGameData endGameData = new EndGameData(time.getTimeMillis(), wonPlayerIds.toArray(new PlayerEndGameData[wonPlayerIds.size()]),
					lostPlayerIds.toArray(new PlayerEndGameData[lostPlayerIds.size()]));
			gameEnded = true;
			unload();
			listeners.forEach(l -> l.gameEnded(endGameData));
		}
	}

	public void unload() {
		chunkManager.setRunning(false);
	}

	/**
	 * Called after all players are added in the EntityPool and Teams are sets. This
	 * will place players and set the map limit if present.
	 * 
	 * @throws MapAnalyticsException
	 */
	public void initializeNewGame() throws MapAnalyticsException {
		entityPool.flushNewEntities();
		gameMode.getPlayerSpawn().apply(this);
		for (PlayerEntity player : getPlayerEntities().values()) {
			player.getSpawnPosition().set(player.getPosition());
		}
		initializeEvents();
		gameMode.getEndGameConditions().forEach(c -> {
			c.initialize(this);
			c.initializeNewGameData(this);
		});
		initializeRoles();
		initializeSystems();
		initializeNewServerWorldSubscription.forEach(InitializeNewServerWorldInterest::initializeNewServerWorld);
	}

	public void initializeLoadedGame() {
		gameMode.getEndGameConditions().forEach(c -> c.initialize(this));
		// TODO Change this when multiplayer save implemented
		setMyPlayer(getPlayerEntities().values().iterator().next());
		playerEntities.put(getMyPlayer().getId(), getMyPlayer());
		entityPool.flushNewEntities();
		initializeSystems();
	}

	public void initializeClientGame(CreateWorld createWorld) {
		initializeSystems();
		initializeNewClientWorldSubscription.forEach(i -> i.initializeNewClientWorld(createWorld));
	}

	public void received(ChatEntry chatEntry) {
		chatManager.received(chatEntry);
		String text = chatEntry.getText();
		if (text.startsWith("/") && text.length() > 1) {
			String returnText = commandManager.process((CommandExecutor) chatEntry.getSender(), CommandArgsUtils.splitArgs(text.substring(1)));
			if (returnText != null) {
				chatManager.received(new ChatEntry(this, returnText));
			}
		}
	}

	private void initializeEvents() {
		for (int i = 0; i < gameMode.getEvents().size(); i++) {
			actionTimerManager.addActionTimer(new EventAction(i), gameMode.getEvents().get(i).getStartTime());
		}
	}

	public void initializeRoles() {
		Roles roles = gameMode.getRoles();
		if (roles != null) {
			roles.apply(this);
		}
	}

	public void writeInventory(ByteBuffer buffer, Inventory inventory) {
		try (Output output = new Output(buffer.array())) {
			output.setPosition(buffer.position());
			getWorld().getInventoryKryo().writeObject(output, inventory);
			buffer.position(output.position());
		}
	}

	public void readInventory(ByteBuffer buffer, Inventory inventory) {
		try (Input input = new Input(buffer.array())) {
			input.setPosition(buffer.position());
			inventory.set(getWorld().getInventoryKryo().readObject(input, inventory.getClass()));
			buffer.position(input.position());
		}
	}

	@Override
	public String getName() {
		return "World";
	}

	@Override
	public boolean isOperator() {
		return true;
	}

	@Override
	public World getWorld() {
		return this;
	}
}
