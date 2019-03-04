package com.pixurvival.contentPackEditor;

import java.util.HashMap;
import java.util.Map;

import com.pixurvival.contentPackEditor.component.RootElementList;
import com.pixurvival.contentPackEditor.component.animationTemplate.AnimationTemplateEditor;
import com.pixurvival.contentPackEditor.component.equipmentOffset.EquipmentOffsetEditor;
import com.pixurvival.contentPackEditor.component.item.ItemEditor;
import com.pixurvival.contentPackEditor.component.itemCraft.ItemCraftEditor;
import com.pixurvival.contentPackEditor.component.itemReward.ItemRewardEditor;
import com.pixurvival.contentPackEditor.component.mapGenerator.MapGeneratorEditor;
import com.pixurvival.contentPackEditor.component.spriteSheet.SpriteSheetEditor;
import com.pixurvival.contentPackEditor.component.structure.StructureEditor;
import com.pixurvival.contentPackEditor.component.tile.TileEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.core.contentPack.IdentifiedElement;
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
	TILE(Tile.class, new TileEditor()),
	STRUCTURE(Structure.class, new StructureEditor()),
	MAP_GENERATOR(MapGenerator.class, new MapGeneratorEditor());

	private static Map<Class<? extends IdentifiedElement>, ElementType> classToType = new HashMap<>();

	static {
		for (ElementType type : ElementType.values()) {
			classToType.put(type.getElementClass(), type);
			type.elementList = new RootElementList<>(type);
		}
	}

	private @NonNull @Getter Class<? extends IdentifiedElement> elementClass;
	@SuppressWarnings("rawtypes")
	private @NonNull @Getter ElementEditor elementEditor;
	private @Getter RootElementList<IdentifiedElement> elementList;

	@Override
	public String toString() {
		return TranslationService.getInstance().getString("elementType." + CaseUtils.upperToCamelCase(name()));
	}

	public static ElementType of(IdentifiedElement element) {
		return classToType.get(element.getClass());
	}
}