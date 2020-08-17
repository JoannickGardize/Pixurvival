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
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.ElementCollection;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.StatFormula;
import com.pixurvival.core.util.IntWrapper;
import com.pixurvival.core.util.ReflectionUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentPack implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient Map<String, byte[]> resources;

	private transient Map<Class<? extends IdentifiedElement>, Map<String, IdentifiedElement>> elementsByName;

	private transient float maxLightRadius;

	private transient Map<Locale, Properties> translations = new HashMap<>();

	private transient Map<Long, StatFormula> statFormulas = new HashMap<>();

	private transient Map<Integer, Alteration> alterations = new HashMap<>();

	private transient ContentPackIdentifier identifier = new ContentPackIdentifier();
	private transient float maxLivingEntityRadius;
	@Getter
	private transient boolean initialized = false;

	@Valid
	@ElementCollection(AnimationTemplate.class)
	private List<AnimationTemplate> animationTemplates = new ArrayList<>();

	@Valid
	@ElementCollection(EquipmentOffset.class)
	private List<EquipmentOffset> equipmentOffsets = new ArrayList<>();

	@Valid
	@ElementCollection(SpriteSheet.class)
	private List<SpriteSheet> spriteSheets = new ArrayList<>();

	@Valid
	@ElementCollection(Effect.class)
	private List<Effect> effects = new ArrayList<>();

	@Valid
	@ElementCollection(Item.class)
	private List<Item> items = new ArrayList<>();

	@Valid
	@ElementCollection(ItemCraft.class)
	private List<ItemCraft> itemCrafts = new ArrayList<>();

	@Valid
	@ElementCollection(ItemReward.class)
	private List<ItemReward> itemRewards = new ArrayList<>();

	@Valid
	@ElementCollection(BehaviorSet.class)
	private List<BehaviorSet> behaviorSets = new ArrayList<>();

	@ElementCollection(AbilitySet.class)
	private List<AbilitySet> abilitySets = new ArrayList<>();

	@Valid
	@ElementCollection(Creature.class)
	private List<Creature> creatures = new ArrayList<>();

	@Valid
	@ElementCollection(Tile.class)
	private List<Tile> tiles = new ArrayList<>();

	@Valid
	@ElementCollection(Structure.class)
	private List<Structure> structures = new ArrayList<>();

	@Valid
	@ElementCollection(MapGenerator.class)
	private List<MapGenerator> mapGenerators = new ArrayList<>();

	@ElementCollection(Ecosystem.class)
	private List<Ecosystem> ecosystems = new ArrayList<>();

	@ElementCollection(GameMode.class)
	private List<GameMode> gameModes = new ArrayList<>();

	@Valid
	@Required
	private Constants constants = new Constants();

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
		return resources.containsKey(resource);
	}

	@SuppressWarnings("unchecked")
	public void initialize() {
		if (initialized) {
			return;
		}
		initialized = true;
		initializeStatFormulaMap();
		initializeAlterationMap();
		elementsByName = new HashMap<>();
		for (Field field : ReflectionUtils.getAnnotedFields(getClass(), ElementCollection.class)) {
			List<IdentifiedElement> list = (List<IdentifiedElement>) ReflectionUtils.getByGetter(this, field);
			Map<String, IdentifiedElement> typeMap = new HashMap<>();
			elementsByName.put(field.getAnnotation(ElementCollection.class).value(), typeMap);
			for (IdentifiedElement element : list) {
				typeMap.put(element.getName(), element);
				element.initialize();
			}
		}
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
		maxLivingEntityRadius = PlayerEntity.COLLISION_RADIUS;
		for (Creature creature : creatures) {
			if (creature.getCollisionRadius() > maxLivingEntityRadius) {
				maxLivingEntityRadius = creature.getCollisionRadius();
			}
		}
	}

	public void initializeStatFormulaMap() {
		statFormulas.clear();
		forEachStatFormulas(f -> statFormulas.put(f.getId(), f));
	}

	public void initializeAlterationMap() {
		IntWrapper nextId = new IntWrapper();
		forEachAlteration(a -> {
			a.setId(nextId.increment());
			alterations.put(a.getId(), a);
		});
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
	public <T extends IdentifiedElement> T get(Class<T> type, String name) {
		Map<String, IdentifiedElement> typeMap = elementsByName.get(type);
		if (typeMap == null) {
			return null;
		} else {
			return (T) typeMap.get(name);
		}
	}

	public Set<String> allNamesOf(Class<? extends IdentifiedElement> type) {
		Map<String, IdentifiedElement> typeMap = elementsByName.get(type);
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

	public static void getAllTranslationKeys(Collection<String> resultStore, IdentifiedElement element) {
		getAllTranslationKeys(resultStore, element, false);

	}

	public static void getAllTranslationKeys(Collection<String> resultStore, IdentifiedElement element, boolean allPotential) {
		if (element instanceof Item) {
			resultStore.add(TranslationKey.NAME.getKey(element));
			resultStore.add(TranslationKey.ITEM_DESCRIPTION.getKey(element));
			if (element instanceof WeaponItem || allPotential) {
				resultStore.add(TranslationKey.ITEM_BASE_ABILITY_NAME.getKey(element));
				resultStore.add(TranslationKey.ITEM_BASE_ABILITY_DESCRIPTION.getKey(element));
			}
			if (element instanceof WeaponItem || element instanceof AccessoryItem || allPotential) {
				resultStore.add(TranslationKey.ITEM_SPECIAL_ABILITY_NAME.getKey(element));
				resultStore.add(TranslationKey.ITEM_SPECIAL_ABILITY_DESCRIPTION.getKey(element));
			}
		} else if (element instanceof Structure || element instanceof GameMode) {
			resultStore.add(TranslationKey.NAME.getKey(element));
		}
	}

	public static List<String> getAllTranslationKeys(IdentifiedElement item) {
		List<String> result = new ArrayList<>();
		getAllTranslationKeys(result, item);
		return result;
	}

	public static List<String> getAllTranslationKeys(IdentifiedElement item, boolean allPotential) {
		List<String> result = new ArrayList<>();
		getAllTranslationKeys(result, item, allPotential);
		return result;
	}

	public String getTranslation(Locale locale, IdentifiedElement item, TranslationKey key) {
		String result = translations.get(locale).getProperty(key.getKey(item));
		return result == null ? "" : result;
	}

	public String getTranslation(Locale locale, String key) {
		String result = translations.get(locale).getProperty(key);
		return result == null ? "" : result;
	}

}
