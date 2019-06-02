package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.contentPack.structure.Structure;

public class StructurePanel extends ItemSpecificPartPanel {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Structure> structureChooser = new ElementChooserButton<>(IconService.getInstance()::get);

	public StructurePanel() {
		EventManager.getInstance().register(this);
		add(LayoutUtils.labelled("elementType.structure", structureChooser));
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		structureChooser.setItems(event.getContentPack().getStructures());
	}

	@Override
	public void bindTo(ItemEditor itemEditor) {
		itemEditor.bind(structureChooser, StructureItem::getStructure, StructureItem::setStructure, StructureItem.class);
	}
}
