package com.pixurvival.core.contentPack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.contentPack.creature.BehaviorSet;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.ElementCollection;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.item.ItemCraft;
import com.pixurvival.core.item.ItemReward;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentPack implements Serializable {

	public static final String SERIALIZATION_ENTRY_NAME = "contentPack";

	private static final long serialVersionUID = 1L;

	private transient Map<String, byte[]> resources;

	@Valid
	@Required
	private ContentPackIdentifier identifier = new ContentPackIdentifier();

	@Valid
	@Required
	private Constants constants = new Constants();

	@Valid
	@ElementCollection(SpriteSheet.class)
	private List<SpriteSheet> spriteSheets = new ArrayList<>();

	@Valid
	@ElementCollection(AnimationTemplate.class)
	private List<AnimationTemplate> animationTemplates = new ArrayList<>();

	@Valid
	@ElementCollection(EquipmentOffset.class)
	private List<EquipmentOffset> equipmentOffsets = new ArrayList<>();

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
}
