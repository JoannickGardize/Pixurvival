package com.pixurvival.contentPackEditor.component.translation;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.ElementRemovedEvent;
import com.pixurvival.contentPackEditor.event.ElementRenamedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ResourceItem;

public class TranslationUpdateManager {

	public TranslationUpdateManager() {
		EventManager.getInstance().register(this);
	}

	@EventListener
	public void elementAdded(ElementAddedEvent event) {
		if (!(event.getElement() instanceof Item)) {
			return;
		}
		Collection<String> keys = ContentPack.getAllTranslationKeys((Item) event.getElement());
		for (Properties properties : FileService.getInstance().getCurrentContentPack().getTranslations().values()) {
			for (String key : keys) {
				properties.put(key, "");
			}
		}
		TranslationDialog.getInstance().notifyDataChanged();
	}

	@EventListener
	public void elementRemoved(ElementRemovedEvent event) {
		if (!(event.getElement() instanceof Item)) {
			return;
		}
		Collection<String> keys = ContentPack.getAllTranslationKeys((Item) event.getElement());
		for (Properties properties : FileService.getInstance().getCurrentContentPack().getTranslations().values()) {
			for (String key : keys) {
				properties.remove(key);
			}
		}
		TranslationDialog.getInstance().notifyDataChanged();
	}

	@EventListener
	public void elementChangedEvent(ElementChangedEvent event) {
		if (!(event.getElement() instanceof Item)) {
			return;
		}
		Collection<String> keys = ContentPack.getAllTranslationKeys((Item) event.getElement());
		Collection<String> allKeys = ContentPack.getAllTranslationKeys((Item) event.getElement(), true);
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
		TranslationDialog.getInstance().notifyDataChanged();
	}

	@EventListener
	public void elementRenamed(ElementRenamedEvent event) {
		if (!(event.getElement() instanceof Item)) {
			return;
		}
		List<String> newKeys = ContentPack.getAllTranslationKeys((Item) event.getElement());
		Item fakeItem = new ResourceItem();
		fakeItem.setName(event.getOldName());
		List<String> oldKeys = ContentPack.getAllTranslationKeys(fakeItem);
		for (Properties properties : FileService.getInstance().getCurrentContentPack().getTranslations().values()) {
			for (int i = 0; i < newKeys.size(); i++) {
				String value = (String) properties.remove(oldKeys.get(i));
				properties.put(newKeys.get(i), value);
			}
		}
		TranslationDialog.getInstance().notifyDataChanged();
	}
}
