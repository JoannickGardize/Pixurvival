package com.pixurvival.contentPackEditor.component.mapProvider;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import java.util.function.Supplier;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.HeightmapCondition;

public class HeightmapConditionEditor extends ElementEditor<HeightmapCondition> {

	private static final long serialVersionUID = 1L;

	public HeightmapConditionEditor(Supplier<Collection<Heightmap>> heightmapCollectionSupplier) {
		super(HeightmapCondition.class);

		// Construction

		ElementChooserButton<Heightmap> heightmapChooser = new ElementChooserButton<>(heightmapCollectionSupplier);
		FloatInput minInput = new FloatInput();
		FloatInput maxInput = new FloatInput();

		// Binding

		bind(heightmapChooser, "heightmap");
		bind(minInput, "min");
		bind(maxInput, "max");

		// Layouting

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		gbc.insets.bottom = 1;
		LayoutUtils.addHorizontalLabelledItem(this, "mapGeneratorEditor.heightmap", heightmapChooser, gbc);
		LayoutUtils.nextColumn(gbc);
		gbc.insets.left = 2;
		LayoutUtils.addHorizontalLabelledItem(this, "generic.minimum", minInput, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "generic.maximum", maxInput, gbc);
	}
}
