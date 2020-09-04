package com.pixurvival.contentPackEditor.component.mapProvider;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.event.ElementInstanceChangedEvent;
import com.pixurvival.contentPackEditor.event.ElementRemovedEvent;
import com.pixurvival.contentPackEditor.event.ElementRenamedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.map.StaticMapProvider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public class StaticMapResourceManager {

	public StaticMapResourceManager() {
		EventManager.getInstance().register(this);
	}

	@EventListener
	public void elementRenamed(ElementRenamedEvent event) {
		if (event.getElement() instanceof StaticMapProvider) {
			ResourcesService rs = ResourcesService.getInstance();

			ResourceEntry entry = rs.deleteResource(StaticMapProvider.getTilesImageResourceName(event.getOldName()));
			if (entry != null) {
				rs.addResource(((StaticMapProvider) event.getElement()).getTilesImageResourceName(), entry.getData());
			}
			entry = rs.deleteResource(StaticMapProvider.getStructuresImageResourceName(event.getOldName()));
			if (entry != null) {
				rs.addResource(((StaticMapProvider) event.getElement()).getStructuresImageResourceName(), entry.getData());
			}
		}
	}

	@EventListener
	public void elementInstanceChanged(ElementInstanceChangedEvent event) {
		if (event.getOldElement() instanceof StaticMapProvider && !(event.getElement() instanceof StaticMapProvider)) {
			removeResources(event.getElement().getName());
		}
	}

	@EventListener
	public void elementRemoved(ElementRemovedEvent event) {
		if (event.getElement() instanceof StaticMapProvider) {
			removeResources(event.getElement().getName());
		}
	}

	private void removeResources(String elementName) {
		ResourcesService rs = ResourcesService.getInstance();
		rs.deleteResource(StaticMapProvider.getTilesImageResourceName(elementName));
		rs.deleteResource(StaticMapProvider.getStructuresImageResourceName(elementName));
	}
}
