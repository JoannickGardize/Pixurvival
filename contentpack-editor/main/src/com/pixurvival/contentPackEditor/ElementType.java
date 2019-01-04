package com.pixurvival.contentPackEditor;

import java.util.HashMap;
import java.util.Map;

import com.pixurvival.contentPackEditor.component.ElementList;
import com.pixurvival.contentPackEditor.component.animationTemplate.AnimationTemplateEditor;
import com.pixurvival.contentPackEditor.component.equipmentOffset.EquipmentOffsetEditor;
import com.pixurvival.contentPackEditor.component.item.ItemEditor;
import com.pixurvival.contentPackEditor.component.itemCraft.ItemCraftEditor;
import com.pixurvival.contentPackEditor.component.itemReward.ItemRewardEditor;
import com.pixurvival.contentPackEditor.component.spriteSheet.SpriteSheetEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.item.ItemCraft;
import com.pixurvival.core.item.ItemReward;
import com.pixurvival.core.util.CaseUtils;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ElementType {
	SPRITE_SHEET(SpriteSheet.class, new SpriteSheetEditor()),
	ANIMATION_TEMPLATE(AnimationTemplate.class, new AnimationTemplateEditor()),
	EQUIPMENT_OFFSET(EquipmentOffset.class, new EquipmentOffsetEditor()),
	ITEM(Item.class, new ItemEditor()),
	ITEM_CRAFT(ItemCraft.class, new ItemCraftEditor()),
	ITEM_REWARD(ItemReward.class, new ItemRewardEditor()),
	TILE(Tile.class, new SpriteSheetEditor()),
	STRUCTURE(Structure.class, new SpriteSheetEditor()),
	MAP_GENERATOR(MapGenerator.class, new SpriteSheetEditor());

	private static Map<Class<? extends NamedElement>, ElementType> classToType = new HashMap<>();

	static {
		for (ElementType type : ElementType.values()) {
			classToType.put(type.getElementClass(), type);
			type.elementList = new ElementList<>(type);
		}
	}

	private @NonNull @Getter Class<? extends NamedElement> elementClass;
	@SuppressWarnings("rawtypes")
	private @NonNull @Getter ElementEditor elementEditor;
	private @Getter ElementList<NamedElement> elementList;

	@Override
	public String toString() {
		return TranslationService.getInstance().getString("elementType." + CaseUtils.upperToCamelCase(name()));
	}

	public static ElementType of(NamedElement element) {
		return classToType.get(element.getClass());
	}
}
