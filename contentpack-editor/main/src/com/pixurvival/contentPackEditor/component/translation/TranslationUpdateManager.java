package com.pixurvival.contentPackEditor.component.translation;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.event.*;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class TranslationUpdateManager {

    public TranslationUpdateManager() {
        EventManager.getInstance().register(this);
    }

    public void refreshAllTranslations() {
        for (ElementType elementType : ElementType.values()) {
            List<NamedIdentifiedElement> list = ContentPackEditionService.getInstance().listOf(elementType);
            for (NamedIdentifiedElement element : list) {
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
    @SneakyThrows
    public void elementRenamed(ElementRenamedEvent event) {
        List<String> newKeys = ContentPack.getAllTranslationKeys(event.getElement());
        NamedIdentifiedElement fakeItem = event.getElement().getClass().newInstance();
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

    private void updateTranslations(NamedIdentifiedElement element) {
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
