package com.pixurvival.contentPackEditor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.ElementRemovedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.util.CaseUtils;

import lombok.Getter;

public class ContentPackEditionService {

	private static @Getter ContentPackEditionService instance = new ContentPackEditionService();

	private Map<ElementType, Method> listGetters = new EnumMap<>(ElementType.class);
	private Map<ElementType, Consumer<NamedElement>> initializers = new EnumMap<>(ElementType.class);

	private ContentPackEditionService() {
		for (ElementType type : ElementType.values()) {
			String methodName = "get" + CaseUtils.upperToPascalCase(type.name()) + "s";
			try {
				listGetters.put(type, ContentPack.class.getMethod(methodName));
			} catch (IllegalArgumentException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		initializers.put(ElementType.ITEM, o -> {
			Item item = (Item) o;
			item.setFrame(new Frame());
			item.setDetails(new Item.Resource());
			item.setMaxStackSize(1);
		});
	}

	public void addElement(ElementType type, String name) {
		if (FileService.getInstance().getCurrentContentPack() == null) {
			return;
		}
		try {
			List<NamedElement> list = listOf(type);
			NamedElement newElement = type.getElementClass().newInstance();
			newElement.setName(name);
			newElement.setId(list.size());
			Consumer<NamedElement> initializer = initializers.get(type);
			if (initializer != null) {
				initializer.accept(newElement);
			}
			list.add(newElement);
			EventManager.getInstance().fire(new ElementAddedEvent(newElement));
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException e) {
			e.printStackTrace();
		}
	}

	public void removeElement(NamedElement element) {
		ElementType type = ElementType.of(element);
		List<NamedElement> list = listOf(type);
		list.remove(element);
		reindex(list);
		EventManager.getInstance().fire(new ElementRemovedEvent(element));
	}

	@SuppressWarnings("unchecked")
	public List<NamedElement> listOf(ElementType type) {
		ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
		if (contentPack == null) {
			return Collections.emptyList();
		}
		try {
			return (List<NamedElement>) listGetters.get(type).invoke(contentPack);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isValidForPreview(SpriteSheet spriteSheet) {
		return spriteSheet != null && spriteSheet.getWidth() > 0 && spriteSheet.getHeight() > 0 && spriteSheet.getImage() != null
				&& ResourcesService.getInstance().getResource(spriteSheet.getImage()) != null;
	}

	private void reindex(List<NamedElement> list) {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setId(i);
		}
	}

}
