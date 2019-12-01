package com.pixurvival.contentPackEditor.component.mapGenerator;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.StringInput;
import com.pixurvival.core.contentPack.map.Heightmap;

public class HeightmapEditor extends ElementEditor<Heightmap> {

	private static final long serialVersionUID = 1L;

	public HeightmapEditor() {

		// Construction

		StringInput nameInput = new StringInput(1);
		IntegerInput numberOfOctaveInput = new IntegerInput(Bounds.min(1));
		FloatInput persistenceInput = new FloatInput(Bounds.positive());
		FloatInput scaleInput = new FloatInput(Bounds.positive());

		// Binding

		bind(nameInput, Heightmap::getName, Heightmap::setName);
		bind(numberOfOctaveInput, Heightmap::getNumberOfoctaves, Heightmap::setNumberOfoctaves);
		bind(persistenceInput, Heightmap::getPersistence, Heightmap::setPersistence);
		bind(scaleInput, Heightmap::getScale, Heightmap::setScale);

		// Layouting

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "generic.name", nameInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "heightmapEditor.persistence", persistenceInput, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "heightmapEditor.numberOfOctave", numberOfOctaveInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "heightmapEditor.scale", scaleInput, gbc);
	}
}
