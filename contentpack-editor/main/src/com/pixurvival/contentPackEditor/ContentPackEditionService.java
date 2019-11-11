package com.pixurvival.contentPackEditor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.contentPackEditor.component.abilitySet.AbilitySetEditor;
import com.pixurvival.contentPackEditor.component.animationTemplate.AnimationTemplateEditor;
import com.pixurvival.contentPackEditor.component.behaviorSet.BehaviorSetEditor;
import com.pixurvival.contentPackEditor.component.creature.CreatureEditor;
import com.pixurvival.contentPackEditor.component.ecosystem.EcosystemEditor;
import com.pixurvival.contentPackEditor.component.effect.EffectEditor;
import com.pixurvival.contentPackEditor.component.equipmentOffset.EquipmentOffsetEditor;
import com.pixurvival.contentPackEditor.component.gameMode.GameModeEditor;
import com.pixurvival.contentPackEditor.component.item.ItemEditor;
import com.pixurvival.contentPackEditor.component.itemCraft.ItemCraftEditor;
import com.pixurvival.contentPackEditor.component.itemReward.ItemRewardEditor;
import com.pixurvival.contentPackEditor.component.mapGenerator.MapGeneratorEditor;
import com.pixurvival.contentPackEditor.component.spriteSheet.SpriteSheetEditor;
import com.pixurvival.contentPackEditor.component.structure.StructureEditor;
import com.pixurvival.contentPackEditor.component.tile.TileEditor;
import com.pixurvival.contentPackEditor.component.translation.TranslationUpdateManager;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.ElementInstanceChangedEvent;
import com.pixurvival.contentPackEditor.event.ElementRemovedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.util.CaseUtils;

import lombok.Getter;
import lombok.SneakyThrows;

public class ContentPackEditionService {

	private static @Getter ContentPackEditionService instance = new ContentPackEditionService();

	private Map<ElementType, Method> listGetters = new EnumMap<>(ElementType.class);
	private Map<ElementType, Method> listSetters = new EnumMap<>(ElementType.class);
	@SuppressWarnings("rawtypes")
	private Map<ElementType, ElementEditor> elementTypeEditors = new EnumMap<>(ElementType.class);

	@SneakyThrows
	private ContentPackEditionService() {
		for (ElementType type : ElementType.values()) {
			String methodName = "get" + CaseUtils.upperToPascalCase(type.name()) + "s";
			listGetters.put(type, ContentPack.class.getMethod(methodName));
			methodName = "set" + CaseUtils.upperToPascalCase(type.name()) + "s";
			listSetters.put(type, ContentPack.class.getMethod(methodName, List.class));
		}
		elementTypeEditors.put(ElementType.SPRITE_SHEET, new SpriteSheetEditor());
		elementTypeEditors.put(ElementType.ANIMATION_TEMPLATE, new AnimationTemplateEditor());
		elementTypeEditors.put(ElementType.EQUIPMENT_OFFSET, new EquipmentOffsetEditor());
		elementTypeEditors.put(ElementType.ITEM, new ItemEditor());
		elementTypeEditors.put(ElementType.ITEM_CRAFT, new ItemCraftEditor());
		elementTypeEditors.put(ElementType.ITEM_REWARD, new ItemRewardEditor());
		elementTypeEditors.put(ElementType.EFFECT, new EffectEditor());
		elementTypeEditors.put(ElementType.ABILITY_SET, new AbilitySetEditor());
		elementTypeEditors.put(ElementType.BEHAVIOR_SET, new BehaviorSetEditor());
		elementTypeEditors.put(ElementType.CREATURE, new CreatureEditor());
		elementTypeEditors.put(ElementType.TILE, new TileEditor());
		elementTypeEditors.put(ElementType.STRUCTURE, new StructureEditor());
		elementTypeEditors.put(ElementType.MAP_GENERATOR, new MapGeneratorEditor());
		elementTypeEditors.put(ElementType.ECOSYSTEM, new EcosystemEditor());
		elementTypeEditors.put(ElementType.GAME_MODE, new GameModeEditor());

		// Register translation related events
		new TranslationUpdateManager();
	}

	@SuppressWarnings("rawtypes")
	public ElementEditor editorOf(ElementType type) {
		return elementTypeEditors.get(type);
	}

	@SneakyThrows
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IdentifiedElement addElement(ElementType type, String name) {
		if (FileService.getInstance().getCurrentContentPack() == null) {
			return null;
		}
		List list = listOf(type);

		IdentifiedElement newElement = BeanFactory.newInstance(type.getElementClass());
		newElement.setName(name);
		newElement.setId(list.size());
		list.add(newElement);
		EventManager.getInstance().fire(new ElementAddedEvent(newElement));
		return newElement;
	}

	public void removeElement(IdentifiedElement element) {
		ElementType type = ElementType.of(element);
		List<? extends IdentifiedElement> list = listOf(type);
		list.remove(element);
		reindex(list);
		EventManager.getInstance().fire(new ElementRemovedEvent(element));
	}

	public void changeInstance(IdentifiedElement element) {
		ElementType type = ElementType.of(element);
		List<IdentifiedElement> list = listOf(type);
		IdentifiedElement oldInstance = list.get(element.getId());
		list.set(element.getId(), element);
		EventManager.getInstance().fire(new ElementInstanceChangedEvent(oldInstance, element));
	}

	@SneakyThrows
	public List<IdentifiedElement> listOf(ElementType type) {
		return listOf(FileService.getInstance().getCurrentContentPack(), type);
	}

	@SuppressWarnings("unchecked")
	@SneakyThrows
	public List<IdentifiedElement> listOf(ContentPack contentPack, ElementType type) {
		if (contentPack == null) {
			return Collections.emptyList();
		}
		List<IdentifiedElement> result = (List<IdentifiedElement>) listGetters.get(type).invoke(contentPack);
		if (result == null) {
			result = new ArrayList<>();
			listSetters.get(type).invoke(contentPack, result);
		}
		return result;
	}

	public boolean isValidForPreview(SpriteSheet spriteSheet) {
		return spriteSheet != null && spriteSheet.getWidth() > 0 && spriteSheet.getHeight() > 0 && spriteSheet.getImage() != null
				&& ResourcesService.getInstance().getResource(spriteSheet.getImage()) != null;
	}

	private void reindex(List<? extends IdentifiedElement> list) {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setId(i);
		}
	}

}
