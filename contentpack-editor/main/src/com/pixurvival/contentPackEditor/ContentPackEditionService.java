package com.pixurvival.contentPackEditor;

import com.pixurvival.contentPackEditor.component.abilitySet.AbilitySetEditor;
import com.pixurvival.contentPackEditor.component.animationTemplate.AnimationTemplateEditor;
import com.pixurvival.contentPackEditor.component.behaviorSet.BehaviorSetEditor;
import com.pixurvival.contentPackEditor.component.creature.CreatureEditor;
import com.pixurvival.contentPackEditor.component.ecosystem.EcosystemEditor;
import com.pixurvival.contentPackEditor.component.effect.EffectEditor;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.equipmentOffset.EquipmentOffsetEditor;
import com.pixurvival.contentPackEditor.component.gameMode.GameModeEditor;
import com.pixurvival.contentPackEditor.component.item.ItemEditor;
import com.pixurvival.contentPackEditor.component.itemCraft.ItemCraftEditor;
import com.pixurvival.contentPackEditor.component.itemReward.ItemRewardEditor;
import com.pixurvival.contentPackEditor.component.mapProvider.MapProviderEditor;
import com.pixurvival.contentPackEditor.component.mapProvider.StaticMapResourceManager;
import com.pixurvival.contentPackEditor.component.spriteSheet.SpriteSheetEditor;
import com.pixurvival.contentPackEditor.component.structure.StructureEditor;
import com.pixurvival.contentPackEditor.component.tile.TileEditor;
import com.pixurvival.contentPackEditor.component.translation.TranslationUpdateManager;
import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.ElementInstanceChangedEvent;
import com.pixurvival.contentPackEditor.event.ElementRemovedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.util.CaseUtils;
import com.pixurvival.core.util.Wrapper;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.*;

public class ContentPackEditionService {

    private static @Getter ContentPackEditionService instance = new ContentPackEditionService();

    private Map<ElementType, Method> listGetters = new EnumMap<>(ElementType.class);
    private Map<ElementType, Method> listSetters = new EnumMap<>(ElementType.class);
    @SuppressWarnings("rawtypes")
    private Map<ElementType, ElementEditor> elementTypeEditors = new EnumMap<>(ElementType.class);
    private long nextStatFormulaId;

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
        elementTypeEditors.put(ElementType.MAP_PROVIDER, new MapProviderEditor());
        elementTypeEditors.put(ElementType.ECOSYSTEM, new EcosystemEditor());
        elementTypeEditors.put(ElementType.GAME_MODE, new GameModeEditor());

        // Register event related managers
        new TranslationUpdateManager();
        new StaticMapResourceManager();
    }

    public void updateNextStatFormulaId() {
        Wrapper<Long> maxId = new Wrapper<>(-1L);
        FileService.getInstance().getCurrentContentPack().forEachStatFormulas(f -> {
            if (f.getId() > maxId.getValue()) {
                maxId.setValue(f.getId());
            }
        });
        nextStatFormulaId = maxId.getValue() + 1;
    }

    public long nextStatFormulaId() {
        return nextStatFormulaId++;
    }

    @SuppressWarnings("rawtypes")
    public ElementEditor editorOf(ElementType type) {
        return elementTypeEditors.get(type);
    }

    @SneakyThrows
    @SuppressWarnings({"rawtypes", "unchecked"})
    public NamedIdentifiedElement addElement(ElementType type, String name) {
        if (FileService.getInstance().getCurrentContentPack() == null) {
            return null;
        }
        List list = listOf(type);

        NamedIdentifiedElement newElement = BeanFactory.newInstance(type.getElementClass());
        newElement.setName(name);
        newElement.setId(list.size());
        list.add(newElement);
        EventManager.getInstance().fire(new ElementAddedEvent(newElement));
        return newElement;
    }

    public void removeElement(NamedIdentifiedElement element) {
        ElementType type = ElementType.of(element);
        List<? extends NamedIdentifiedElement> list = listOf(type);
        list.remove(element);
        reindex(list);
        EventManager.getInstance().fire(new ElementRemovedEvent(element));
    }

    public void changeInstance(NamedIdentifiedElement element) {
        ElementType type = ElementType.of(element);
        List<NamedIdentifiedElement> list = listOf(type);
        NamedIdentifiedElement oldInstance = list.get(element.getId());
        list.set(element.getId(), element);
        EventManager.getInstance().fire(new ElementInstanceChangedEvent(oldInstance, element));
    }

    @SneakyThrows
    public List<NamedIdentifiedElement> listOf(ElementType type) {
        return listOf(FileService.getInstance().getCurrentContentPack(), type);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public List<NamedIdentifiedElement> listOf(ContentPack contentPack, ElementType type) {
        if (contentPack == null) {
            return Collections.emptyList();
        }
        List<NamedIdentifiedElement> result = (List<NamedIdentifiedElement>) listGetters.get(type).invoke(contentPack);
        if (result == null) {
            result = new ArrayList<>();
            listSetters.get(type).invoke(contentPack, result);
        }
        return result;
    }

    public boolean isEmpty() {
        if (FileService.getInstance().getCurrentContentPack() == null) {
            return true;
        }
        for (ElementType elementType : ElementType.values()) {
            if (listOf(elementType).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidForPreview(SpriteSheet spriteSheet) {
        return spriteSheet != null && spriteSheet.getWidth() > 0 && spriteSheet.getHeight() > 0 && spriteSheet.getImage() != null
                && ResourcesService.getInstance().getResource(spriteSheet.getImage()) != null;
    }

    private void reindex(List<? extends NamedIdentifiedElement> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setId(i);
        }
    }

}
