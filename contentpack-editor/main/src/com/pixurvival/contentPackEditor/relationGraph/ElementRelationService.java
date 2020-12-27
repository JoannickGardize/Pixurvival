package com.pixurvival.contentPackEditor.relationGraph;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.ElementInstanceChangedEvent;
import com.pixurvival.contentPackEditor.event.ElementRemovedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;

import lombok.Getter;

public class ElementRelationService {

	@Getter
	private static final ElementRelationService instance = new ElementRelationService();

	private ElementRelationExplorer explorer = new ElementRelationExplorer();

	private Map<NamedIdentifiedElement, RelationEntry> relations = new LinkedHashMap<>();

	private ElementRelationService() {
		EventManager.getInstance().register(this);
	}

	public void forEachReferent(NamedIdentifiedElement referenced, Consumer<NamedIdentifiedElement> referentAction) {
		for (RelationEntry entry : relations.values()) {
			if (updatedRelations(entry).contains(referenced)) {
				referentAction.accept(entry.getElement());
			}
		}
	}

	@EventListener
	public void elementChanged(ElementChangedEvent event) {
		RelationEntry entry = relations.get(event.getElement());
		if (entry != null) {
			entry.setUpToDate(false);
		}
	}

	@EventListener
	public void elementInstanceChanged(ElementInstanceChangedEvent event) {
		relations.remove(event.getElement());
	}

	@EventListener
	public void elementRemoved(ElementRemovedEvent event) {
		relations.remove(event.getElement());
	}

	@EventListener
	public void elementAddedEvent(ElementAddedEvent event) {
		relations.put(event.getElement(), new RelationEntry(event.getElement()));
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		event.getContentPack().initialize();
		relations.clear();
		for (ElementType elementType : ElementType.values()) {
			event.getContentPack().listOf(elementType.getElementClass()).forEach(e -> relations.put(e, new RelationEntry(e)));
		}
	}

	private Set<NamedIdentifiedElement> updatedRelations(RelationEntry entry) {
		if (!entry.isUpToDate()) {
			explorer.update(entry.getElement(), entry.getRelations());
			entry.setUpToDate(true);
		}
		return entry.getRelations();
	}
}
