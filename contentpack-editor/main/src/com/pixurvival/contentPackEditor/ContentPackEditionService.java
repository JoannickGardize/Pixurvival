package com.pixurvival.contentPackEditor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.NamedElement;

import lombok.Getter;

public class ContentPackEditionService {

	private static @Getter ContentPackEditionService instance = new ContentPackEditionService();

	private Map<ElementType, ElementEditionEntry> elementEntries = new HashMap<>();

	private ContentPackEditionService() {
		for (ElementType elementType : ElementType.values()) {
			elementEntries.put(elementType, new ElementEditionEntry(elementType));
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
			newElement.setIndex(list.size());
			list.add(newElement);
			EventManager.getInstance().fire(new ElementAddedEvent(newElement));
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException e) {
			e.printStackTrace();
		}
	}

	public void removeElement(NamedElement element) {

	}

	public boolean isValid(NamedElement element) {
		return elementEntries.get(ElementType.typeOf(element)).isValid(element);
	}

	public List<NamedElement> listOf(ElementType type) {
		return elementEntries.get(type).getList();
	}

}
