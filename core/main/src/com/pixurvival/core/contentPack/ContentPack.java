package com.pixurvival.core.contentPack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.Tile;
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

	private transient Map<String, byte[]> resources = new HashMap<>();

	private ContentPackIdentifier identifier;
	private List<SpriteSheet> spriteSheets = new ArrayList<>();
	private List<Tile> tiles = new ArrayList<>();
	private List<Item> items = new ArrayList<>();
	private List<ItemReward> itemRewards = new ArrayList<>();
	private List<Structure> structures = new ArrayList<>();
	private List<MapGenerator> mapGenerators = new ArrayList<>();
	private List<ItemCraft> itemCrafts = new ArrayList<>();
	private List<EquipmentOffset> equipmentOffsets = new ArrayList<>();
	private Constants constants = new Constants();

	public byte[] getResource(String resource) {
		return resources.get(resource);
	}

	public void addResource(String resource, byte[] data) {
		resources.put(resource, data);
	}

}
