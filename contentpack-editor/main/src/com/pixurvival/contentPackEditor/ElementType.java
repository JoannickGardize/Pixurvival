package com.pixurvival.contentPackEditor;

import java.util.HashMap;
import java.util.Map;

import com.pixurvival.contentPackEditor.component.ElementEditor;
import com.pixurvival.contentPackEditor.component.SpriteSheetEditor;
import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.util.CaseUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ElementType {
	SPRITE_SHEET(SpriteSheet.class, SpriteSheetEditor.class),
	ANIMATION_TEMPLATE(AnimationTemplate.class, SpriteSheetEditor.class),
	EQUIPMENT_OFFSET(EquipmentOffset.class, SpriteSheetEditor.class),
	ITEM(Item.class, SpriteSheetEditor.class),
	ITEM_CRAFT(ItemCraft.class, SpriteSheetEditor.class),
	ITEM_REWARD(ItemReward.class, SpriteSheetEditor.class),
	TILE(Tile.class, SpriteSheetEditor.class),
	STRUCTURE(Structure.class, SpriteSheetEditor.class),
	MAP_GENERATOR(MapGenerator.class, SpriteSheetEditor.class);

	private static Map<Class<? extends NamedElement>, ElementType> classToType = new HashMap<>();

	static {
		for (ElementType type : ElementType.values()) {
			classToType.put(type.getElementClass(), type);
		}
	}

	private @Getter Class<? extends NamedElement> elementClass;
	private @Getter Class<? extends ElementEditor<?>> elementEditor;

	@Override
	public String toString() {
		return TranslationService.getInstance().getString("elementType." + CaseUtils.upperToCamelCase(name()));
	}

	public static ElementType typeOf(NamedElement element) {
		return classToType.get(element.getClass());
	}
}
