package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.contentPack.structure.Structure;

public class StructurePanel extends ItemSpecificPartPanel {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Structure> structureChooser = new ElementChooserButton<>(Structure.class);

	public StructurePanel() {
		add(LayoutUtils.labelled("elementType.structure", structureChooser));
	}

	@Override
	public void bindTo(ItemEditor itemEditor) {
		itemEditor.bind(structureChooser, StructureItem::getStructure, StructureItem::setStructure, StructureItem.class);
	}
}
