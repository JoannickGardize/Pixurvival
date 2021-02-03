package com.pixurvival.core.contentPack;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

import com.pixurvival.core.SoundPreset;
import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.alteration.PlayCustomSoundAlteration;
import com.pixurvival.core.alteration.StatFormula;
import com.pixurvival.core.contentPack.creature.BehaviorSet;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.contentPack.map.MapProvider;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.ElementList;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.util.IntWrapper;
import com.pixurvival.core.util.ReflectionUtils;
import com.pixurvival.core.util.ReleaseVersion;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentPack implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient Map<String, byte[]> resources;

	private transient Map<Class<? extends NamedIdentifiedElement>, Map<String, NamedIdentifiedElement>> elementsByName;
	private transient Map<Class<? extends NamedIdentifiedElement>, List<NamedIdentifiedElement>> elementsLists;

	private transient float maxLightRadius;

	private transient Map<Locale, Properties> translations = new HashMap<>();

	private transient Map<Long, StatFormula> statFormulas = new HashMap<>();

	private transient Map<Integer, Alteration> alterations = new HashMap<>();

	private transient Map<String, Integer> soundIdByName;

	@Valid
	private ContentPackIdentifier identifier;

	private transient float maxLivingEntityRadius;
	@Getter
	private transient boolean initialized = false;

	@Valid
	@ElementList(AnimationTemplate.class)
	private List<AnimationTemplate> animationTemplates = new ArrayList<>();

	@Valid
	@ElementList(EquipmentOffset.class)
	private List<EquipmentOffset> equipmentOffsets = new ArrayList<>();

	@Valid
	@ElementList(SpriteSheet.class)
	private List<SpriteSheet> spriteSheets = new ArrayList<>();

	@Valid
	@ElementList(Effect.class)
	private List<Effect> effects = new ArrayList<>();

	@Valid
	@ElementList(Item.class)
	private List<Item> items = new ArrayList<>();

	@Valid
	@ElementList(ItemCraft.class)
	private List<ItemCraft> itemCrafts = new ArrayList<>();

	@Valid
	@ElementList(ItemReward.class)
	private List<ItemReward> itemRewards = new ArrayList<>();

	@Valid
	@ElementList(BehaviorSet.class)
	private List<BehaviorSet> behaviorSets = new ArrayList<>();

	@Valid
	@ElementList(AbilitySet.class)
	private List<AbilitySet> abilitySets = new ArrayList<>();

	@Valid
	@ElementList(Creature.class)
	private List<Creature> creatures = new ArrayList<>();

	@Valid
	@ElementList(Tile.class)
	private List<Tile> tiles = new ArrayList<>();

	@Valid
	@ElementList(Structure.class)
	private List<Structure> structures = new ArrayList<>();

	@Valid
	@ElementList(MapProvider.class)
	private List<MapProvider> mapProviders = new ArrayList<>();

	@Valid
	@ElementList(Ecosystem.class)
	private List<Ecosystem> ecosystems = new ArrayList<>();

	@Valid
	@Length(min = 1)
	@ElementList(GameMode.class)
	private List<GameMode> gameModes = new ArrayList<>();

	@Valid
	private Constants constants = new Constants();

	/**
	 * @return The release version this pack was made with. represents the name of
	 *         an enum value of {@link ReleaseVersion}, in this way opening a pack
	 *         of an unknown newer version will not crash. Null value means that the
	 *         pack is prior to the {@link ReleaseVersion#ALPHA_5} version.
	 */
	private String releaseVersion;

	public byte[] getResource(String resource) {
		if (resources == null) {
			return null;
		}
		return resources.get(resource);
	}

	public void addResource(String resource, byte[] data) {
		if (resources == null) {
			resources = new HashMap<>();
		}
		resources.put(resource, data);
	}

	public void addTranslation(Locale locale, Properties file) {
		translations.put(locale, file);
	}

	public void removeResource(String resource) {
		resources.remove(resource);
	}

	public Map<String, byte[]> getResources() {
		if (resources == null) {
			return Collections.emptyMap();
		} else {
			return resources;
		}
	}

	public boolean containsResource(String resource) {
		return resources != null && resources.containsKey(resource);
	}

	public void initialize() {
		if (initialized) {
			return;
		}
		initialized = true;
		initializeStatFormulaMap();
		initializeAlterationMap();
		initializeCustomSoundsArray();
		elementsByName = new HashMap<>();
		elementsLists = new HashMap<>();
		callElementsInitializeMethod();
		initializeStructures();
		computeMaxLivingEntityRadius();
	}

	public List<NamedIdentifiedElement> listOf(Class<? extends NamedIdentifiedElement> type) {
		return elementsLists.get(ReflectionUtils.getSuperClassUnder(type, NamedIdentifiedElement.class));
	}

	private void computeMaxLivingEntityRadius() {
		maxLivingEntityRadius = PlayerEntity.COLLISION_RADIUS;
		for (Creature creature : creatures) {
			if (creature.getCollisionRadius() > maxLivingEntityRadius) {
				maxLivingEntityRadius = creature.getCollisionRadius();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void callElementsInitializeMethod() {
		for (Field field : ReflectionUtils.getAnnotedFields(getClass(), ElementList.class)) {
			List<NamedIdentifiedElement> list = (List<NamedIdentifiedElement>) ReflectionUtils.getByGetter(this, field);
			Map<String, NamedIdentifiedElement> typeMap = new HashMap<>();
			Class<? extends NamedIdentifiedElement> type = field.getAnnotation(ElementList.class).value();
			elementsLists.put(type, list);
			elementsByName.put(type, typeMap);
			for (NamedIdentifiedElement element : list) {
				typeMap.put(element.getName(), element);
				element.initialize();
			}
		}
	}

	private void initializeStructures() {
		for (Structure structure : structures) {
			if (structure.getLightEmissionRadius() > maxLightRadius) {
				maxLightRadius = structure.getLightEmissionRadius();
			}
			if (structure.getDeconstructionDuration() > 0) {
				for (Item item : items) {
					if (item instanceof StructureItem && ((StructureItem) item).getStructure() == structure) {
						structure.setDeconstructionItem(item);
						break;
					}
				}
			}
		}
	}

	private void initializeStatFormulaMap() {
		statFormulas.clear();
		forEachStatFormulas(f -> statFormulas.put(f.getId(), f));
	}

	private void initializeAlterationMap() {
		IntWrapper nextId = new IntWrapper();
		forEachAlteration(a -> {
			a.setId(nextId.increment());
			alterations.put(a.getId(), a);
		});
	}

	private void initializeCustomSoundsArray() {
		soundIdByName = new HashMap<>();
		for (Alteration alteration : alterations.values()) {
			if (alteration instanceof PlayCustomSoundAlteration) {
				PlayCustomSoundAlteration playCustomSoundAlteration = (PlayCustomSoundAlteration) alteration;
				Integer soundId = soundIdByName.get(playCustomSoundAlteration.getSound());
				if (soundId == null) {
					soundId = soundIdByName.size() + SoundPreset.values().length;
					soundIdByName.put(playCustomSoundAlteration.getSound(), soundId);
				}
				playCustomSoundAlteration.setSoundId(soundId);
			}
		}
	}

	public void forEachStatFormulas(Consumer<StatFormula> action) {
		items.forEach(i -> i.forEachStatFormula(action));
		creatures.forEach(c -> c.forEachStatFormula(action));
		effects.forEach(e -> e.forEachStatFormula(action));
	}

	private void forEachAlteration(Consumer<Alteration> action) {
		items.forEach(i -> i.forEachAlteration(action));
		creatures.forEach(c -> c.forEachAlteration(action));
		effects.forEach(e -> e.forEachAlteration(action));
	}

	@SuppressWarnings("unchecked")
	public <T extends NamedIdentifiedElement> T get(Class<T> type, String name) {
		Map<String, NamedIdentifiedElement> typeMap = elementsByName.get(type);
		if (typeMap == null) {
			return null;
		} else {
			return (T) typeMap.get(name);
		}
	}

	public Set<String> allNamesOf(Class<? extends NamedIdentifiedElement> type) {
		Map<String, NamedIdentifiedElement> typeMap = elementsByName.get(type);
		if (typeMap == null) {
			return Collections.emptySet();
		} else {
			return typeMap.keySet();
		}
	}

	public List<String> getAllTranslationKeys() {
		List<String> result = new ArrayList<>();
		for (Item item : items) {
			getAllTranslationKeys(result, item);
		}
		for (Structure structure : structures) {
			getAllTranslationKeys(result, structure);
		}
		for (GameMode gameMode : gameModes) {
			getAllTranslationKeys(result, gameMode);
		}
		return result;
	}

	public static void getAllTranslationKeys(Collection<String> resultStore, NamedIdentifiedElement element) {
		getAllTranslationKeys(resultStore, element, false);

	}

	public static void getAllTranslationKeys(Collection<String> resultStore, NamedIdentifiedElement element, boolean allPotential) {
		if (element instanceof Item) {
			resultStore.add(TranslationKey.NAME.getKey(element));
			resultStore.add(TranslationKey.DESCRIPTION.getKey(element));
			if (element instanceof WeaponItem || allPotential) {
				resultStore.add(TranslationKey.ITEM_BASE_ABILITY_NAME.getKey(element));
				resultStore.add(TranslationKey.ITEM_BASE_ABILITY_DESCRIPTION.getKey(element));
			}
			if (element instanceof WeaponItem || element instanceof AccessoryItem || allPotential) {
				resultStore.add(TranslationKey.ITEM_SPECIAL_ABILITY_NAME.getKey(element));
				resultStore.add(TranslationKey.ITEM_SPECIAL_ABILITY_DESCRIPTION.getKey(element));
			}
		} else if (element instanceof Structure) {
			resultStore.add(TranslationKey.NAME.getKey(element));
		} else if (element instanceof GameMode) {
			resultStore.add(TranslationKey.NAME.getKey(element));
			resultStore.add(TranslationKey.DESCRIPTION.getKey(element));
		}
	}

	public static List<String> getAllTranslationKeys(NamedIdentifiedElement item) {
		List<String> result = new ArrayList<>();
		getAllTranslationKeys(result, item);
		return result;
	}

	public static List<String> getAllTranslationKeys(NamedIdentifiedElement item, boolean allPotential) {
		List<String> result = new ArrayList<>();
		getAllTranslationKeys(result, item, allPotential);
		return result;
	}

	public String getTranslation(Locale locale, NamedIdentifiedElement item, TranslationKey key) {
		String result = translations.get(locale).getProperty(key.getKey(item));
		return result == null ? "" : result;
	}

	public String getTranslation(Locale locale, String key) {
		String result = translations.get(locale).getProperty(key);
		return result == null ? "" : result;
	}

}
