package com.pixurvival.contentPackEditor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.util.CaseUtils;

import lombok.Getter;

public class ContentPackEditionService {

	private static @Getter ContentPackEditionService instance = new ContentPackEditionService();

	private Map<ElementType, Method> listGetters = new EnumMap<>(ElementType.class);

	private ContentPackEditionService() {
		for (ElementType type : ElementType.values()) {
			String methodName = "get" + CaseUtils.upperToPascalCase(type.name()) + "s";
			try {
				listGetters.put(type, ContentPack.class.getMethod(methodName));
			} catch (IllegalArgumentException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
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
			list.add(newElement);
			EventManager.getInstance().fire(new ElementAddedEvent(newElement));
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException e) {
			e.printStackTrace();
		}
	}

	public void removeElement(NamedElement element) {

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

	public boolean isValid(NamedElement spriteSheet) {
		return spriteSheet.isValid(FileService.getInstance().getCurrentContentPack());
	}

	public boolean isValidForPreview(SpriteSheet spriteSheet) {
		return spriteSheet != null && spriteSheet.getWidth() > 0 && spriteSheet.getHeight() > 0 && spriteSheet.getImage() != null
				&& ResourcesService.getInstance().getResource(spriteSheet.getImage()) != null;
	}

	public boolean isAllValid(ElementType elementType) {
		ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
		List<NamedElement> elements = listOf(elementType);
		for (NamedElement element : elements) {
			if (!element.isValid(contentPack)) {
				return false;
			}
		}
		return true;
	}

}
