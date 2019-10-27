package com.pixurvival.contentPackEditor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.pixurvival.contentPackEditor.component.translation.TranslationUpdateManager;
import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.ElementInstanceChangedEvent;
import com.pixurvival.contentPackEditor.event.ElementRemovedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ResourceItem;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.util.CaseUtils;

import lombok.Getter;
import lombok.SneakyThrows;

public class ContentPackEditionService {

	private static @Getter ContentPackEditionService instance = new ContentPackEditionService();

	private Map<ElementType, Method> listGetters = new EnumMap<>(ElementType.class);
	private Map<ElementType, Method> listSetters = new EnumMap<>(ElementType.class);
	private Map<ElementType, Supplier<IdentifiedElement>> initializers = new EnumMap<>(ElementType.class);

	@SneakyThrows
	private ContentPackEditionService() {
		for (ElementType type : ElementType.values()) {
			String methodName = "get" + CaseUtils.upperToPascalCase(type.name()) + "s";
			listGetters.put(type, ContentPack.class.getMethod(methodName));
			methodName = "set" + CaseUtils.upperToPascalCase(type.name()) + "s";
			listSetters.put(type, ContentPack.class.getMethod(methodName, List.class));
		}
		initializers.put(ElementType.ITEM, () -> {
			Item item = new ResourceItem();
			item.setFrame(new Frame());
			item.setMaxStackSize(1);
			return item;
		});

		initializers.put(ElementType.STRUCTURE, Structure::new);

		// Register translation related events
		new TranslationUpdateManager();
	}

	@SneakyThrows
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IdentifiedElement addElement(ElementType type, String name) {
		if (FileService.getInstance().getCurrentContentPack() == null) {
			return null;
		}
		List list = listOf(type);
		Supplier<IdentifiedElement> initializer = initializers.get(type);
		IdentifiedElement newElement;
		if (initializer == null) {
			newElement = type.getElementClass().newInstance();
		} else {
			newElement = initializer.get();
		}
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
