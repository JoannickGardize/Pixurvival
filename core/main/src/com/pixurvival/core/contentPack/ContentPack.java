package com.pixurvival.core.contentPack;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pixurvival.core.contentPack.creature.BehaviorSet;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.ElementCollection;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.EffectAbility;
import com.pixurvival.core.util.ReflectionUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentPack implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient Map<String, byte[]> resources;

	private transient Map<Class<? extends IdentifiedElement>, Map<String, IdentifiedElement>> elementsByName;

	@Valid
	@Required
	private ContentPackIdentifier identifier = new ContentPackIdentifier();

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
	private List<AbilitySet<EffectAbility>> abilitySets = new ArrayList<>();

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

	public boolean isResourcePresent(String resource) {
		return resources.containsKey(resource);
	}

	@SuppressWarnings("unchecked")
	public void initialize() {
		elementsByName = new HashMap<>();
		for (Field field : ReflectionUtils.getAnnotedFields(getClass(), ElementCollection.class)) {
			List<IdentifiedElement> list = (List<IdentifiedElement>) ReflectionUtils.getByGetter(this, field);
			Map<String, IdentifiedElement> typeMap = new HashMap<>();
			elementsByName.put(field.getAnnotation(ElementCollection.class).value(), typeMap);
			for (IdentifiedElement element : list) {
				typeMap.put(element.getName(), element);
			}
		}
		ecosystems.forEach(Ecosystem::initialize);
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
}
