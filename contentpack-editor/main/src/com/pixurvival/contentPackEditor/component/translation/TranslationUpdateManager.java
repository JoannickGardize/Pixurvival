package com.pixurvival.contentPackEditor.component.translation;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.ElementRemovedEvent;
import com.pixurvival.contentPackEditor.event.ElementRenamedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ResourceItem;

public class TranslationUpdateManager {

	public TranslationUpdateManager() {
		EventManager.getInstance().register(this);
	}

	public void refreshAllTranslations() {
		for (ElementType elementType : ElementType.values()) {
			List<IdentifiedElement> list = ContentPackEditionService.getInstance().listOf(elementType);
			for (IdentifiedElement element : list) {
				updateTranslations(element);
			}
		}
	}

	@EventListener
	public void elementAdded(ElementAddedEvent event) {
		Collection<String> keys = ContentPack.getAllTranslationKeys(event.getElement());
		for (Properties properties : FileService.getInstance().getCurrentContentPack().getTranslations().values()) {
			for (String key : keys) {
				properties.put(key, "");
			}
		}
		if (!keys.isEmpty()) {
			TranslationDialog.getInstance().notifyDataChanged();
		}
	}

	@EventListener
	public void elementRemoved(ElementRemovedEvent event) {
		Collection<String> keys = ContentPack.getAllTranslationKeys(event.getElement());
		for (Properties properties : FileService.getInstance().getCurrentContentPack().getTranslations().values()) {
			for (String key : keys) {
				properties.remove(key);
			}
		}
		if (!keys.isEmpty()) {
			TranslationDialog.getInstance().notifyDataChanged();
		}
	}

	@EventListener
	public void elementChangedEvent(ElementChangedEvent event) {
		updateTranslations(event.getElement());
	}

	@EventListener
	public void elementRenamed(ElementRenamedEvent event) {
		List<String> newKeys = ContentPack.getAllTranslationKeys(event.getElement());
		Item fakeItem = new ResourceItem();
		fakeItem.setName(event.getOldName());
		List<String> oldKeys = ContentPack.getAllTranslationKeys(fakeItem);
		for (Properties properties : FileService.getInstance().getCurrentContentPack().getTranslations().values()) {
			for (int i = 0; i < newKeys.size(); i++) {
				String value = (String) properties.remove(oldKeys.get(i));
				properties.put(newKeys.get(i), value);
			}
		}
		if (!newKeys.isEmpty()) {
			TranslationDialog.getInstance().notifyDataChanged();
		}
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		refreshAllTranslations();
	}

	private void updateTranslations(IdentifiedElement element) {
		Collection<String> keys = ContentPack.getAllTranslationKeys(element);
		Collection<String> allKeys = ContentPack.getAllTranslationKeys(element, true);
		for (Properties properties : FileService.getInstance().getCurrentContentPack().getTranslations().values()) {
			for (String key : allKeys) {
				String originalValue = (String) properties.remove(key);
				if (keys.contains(key)) {
					if (originalValue == null) {
						originalValue = "";
					}
					properties.setProperty(key, originalValue);
				}
			}
		}
		if (!keys.isEmpty()) {
			TranslationDialog.getInstance().notifyDataChanged();
		}
	}
}
