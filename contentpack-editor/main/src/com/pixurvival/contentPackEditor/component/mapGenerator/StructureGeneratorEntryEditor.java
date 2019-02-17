package com.pixurvival.contentPackEditor.component.mapGenerator;

import java.util.Collection;

import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.StructureGeneratorEntry;

public class StructureGeneratorEntryEditor extends ElementEditor<StructureGeneratorEntry> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Structure> structureChooser = new ElementChooserButton<>(
			IconService.getInstance()::get);

	public StructureGeneratorEntryEditor() {
		DoubleInput probability = new DoubleInput(Bounds.positive());

		bind(structureChooser, StructureGeneratorEntry::getStructure, StructureGeneratorEntry::setStructure);
		bind(probability, StructureGeneratorEntry::getProbability, StructureGeneratorEntry::setProbability);

		add(structureChooser);
		add(probability);
	}

	public void setStructureCollection(Collection<Structure> structures) {
		structureChooser.setItems(structures);
	}

}
