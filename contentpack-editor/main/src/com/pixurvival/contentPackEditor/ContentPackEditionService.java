package com.pixurvival.contentPackEditor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.contentPackEditor.component.ElementType;
import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.Structure.Harvestable;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.map.TileGenerator;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.item.Item.Equipable;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.util.CaseUtils;

import lombok.Getter;

public class ContentPackEditionService {

	private static @Getter ContentPackEditionService instance = new ContentPackEditionService();

	private Map<Class<? extends NamedElement>, Method> getterMap = new HashMap<>();

	private ContentPackEditionService() {
		for (ElementType elementType : ElementType.values()) {
			String methodName = CaseUtils.upperToCamelCase("GET_" + elementType.name()) + "s";
			for (Method method : ContentPack.class.getMethods()) {
				if (method.getName().equals(methodName)) {
					getterMap.put(elementType.getElementClass(), method);
					break;
				}
			}
		}
	}

	public void addElement(ElementType type, String name) {
		if (FileService.getInstance().getCurrentContentPack() == null) {
			return;
		}
		try {
			List<NamedElement> list = getListOf(type);
			NamedElement newElement = type.getElementClass().newInstance();
			newElement.setName(name);
			list.add(newElement);
			EventManager.getInstance().fire(new ElementAddedEvent(newElement));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| InstantiationException e) {
			e.printStackTrace();
		}
	}

	public void removeElement(NamedElement element) {
		// TODO verifier les refs
		List<NamedElement> list = getListOf(element.getClass());
		list.remove(element);
		ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
		List<NamedElement> changeList = new ArrayList<>();
		if (element instanceof SpriteSheet) {
			for (Item item : contentPack.getItems()) {
				if (item.getDetails() instanceof Equipable) {
					Equipable equipable = (Equipable) item.getDetails();
					if (equipable.getSpriteSheet() == element) {
						equipable.setSpriteSheet(null);
						changeList.add(item);
					}
				}
			}
			for (Structure structure : contentPack.getStructures()) {
				if (structure.getSpriteSheet() == element) {
					structure.setSpriteSheet(null);
					changeList.add(structure);
				}
			}
			if (contentPack.getConstants().getDefaultCharacter() == element) {
				contentPack.getConstants().setDefaultCharacter(null);
			}
		} else if (element instanceof AnimationTemplate) {
			for (SpriteSheet spriteSheet : contentPack.getSpriteSheets()) {
				if (spriteSheet.getAnimationTemplate() == element) {
					spriteSheet.setAnimationTemplate(null);
					changeList.add(spriteSheet);
				}
			}
		} else if (element instanceof EquipmentOffset) {
			for (SpriteSheet spriteSheet : contentPack.getSpriteSheets()) {
				if (spriteSheet.getEquipmentOffset() == element) {
					spriteSheet.setEquipmentOffset(null);
					changeList.add(spriteSheet);
				}
			}
		} else if (element instanceof Item) {
			for (ItemCraft itemCraft : contentPack.getItemCrafts()) {
				List<ItemStack> itemStackList = itemCraft.getRecipes();
				for (int i = 0; i < itemStackList.size(); i++) {
					if (itemStackList.get(i).getItem() == element) {
						itemStackList.remove(i);
						changeList.add(itemCraft);
						break;
					}
				}
			}
			for (ItemReward itemReward : contentPack.getItemRewards()) {
				List<ItemReward.Entry> entries = itemReward.getEntries();
				for (int i = 0; i < entries.size(); i++) {
					if (entries.get(i).getItemStack().getItem() == element) {
						entries.remove(i);
						changeList.add(itemReward);
						break;
					}
				}
			}
		} else if (element instanceof ItemReward) {
			for (Structure structure : contentPack.getStructures()) {
				if (structure.getDetails() instanceof Harvestable) {
					((Harvestable) structure.getDetails()).setItemReward(null);
				}
			}
		} else if (element instanceof Tile) {
			for (MapGenerator mapGenerator : contentPack.getMapGenerators()) {
				List<TileGenerator> tileGenerators = mapGenerator.getTileGenerators();

				for (int i = 0; i < tileGenerators.size(); i++) {
					if (tileGenerators.get(i).getTile() == element) {
						tileGenerators.remove(i);
						i--;
						// TODO not finished
					}
				}
			}
		}
	}

	private List<NamedElement> getListOf(ElementType type) throws IllegalAccessException, InvocationTargetException {
		return getListOf(type.getElementClass());
	}

	@SuppressWarnings("unchecked")
	private List<NamedElement> getListOf(Class<? extends NamedElement> type) {
		ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
		try {
			return (List<NamedElement>) getterMap.get(type).invoke(contentPack);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
