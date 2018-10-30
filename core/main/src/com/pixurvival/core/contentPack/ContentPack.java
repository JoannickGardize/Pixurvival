package com.pixurvival.core.contentPack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.Item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentPack implements Serializable {

	public static final String SERIALIZATION_ENTRY_NAME = "contentPack";

	private static final long serialVersionUID = 1L;

	private transient Map<String, byte[]> resources;

	private ContentPackIdentifier identifier = new ContentPackIdentifier();
	private List<SpriteSheet> spriteSheets = new ArrayList<>();
	private List<AnimationTemplate> animationTemplates = new ArrayList<>();
	private List<EquipmentOffset> equipmentOffsets = new ArrayList<>();
	private List<Item> items = new ArrayList<>();
	private List<ItemCraft> itemCrafts = new ArrayList<>();
	private List<ItemReward> itemRewards = new ArrayList<>();
	private List<Tile> tiles = new ArrayList<>();
	private List<Structure> structures = new ArrayList<>();
	private List<MapGenerator> mapGenerators = new ArrayList<>();
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

	public Map<String, byte[]> getResources() {
		if (resources == null) {
			return Collections.emptyMap();
		} else {
			return resources;
		}
	}

}
