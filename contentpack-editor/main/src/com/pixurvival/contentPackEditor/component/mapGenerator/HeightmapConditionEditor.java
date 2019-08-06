package com.pixurvival.contentPackEditor.component.mapGenerator;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import java.util.function.Supplier;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.HeightmapCondition;

public class HeightmapConditionEditor extends ElementEditor<HeightmapCondition> {

	private static final long serialVersionUID = 1L;

	public HeightmapConditionEditor(Supplier<Collection<Heightmap>> heightmapCollectionSupplier) {

		// Construction

		ElementChooserButton<Heightmap> heightmapChooser = new ElementChooserButton<>(heightmapCollectionSupplier);
		DoubleInput minInput = new DoubleInput(new Bounds(0, 1));
		DoubleInput maxInput = new DoubleInput(new Bounds(0, 1));

		// Binding

		bind(heightmapChooser, HeightmapCondition::getHeightmap, HeightmapCondition::setHeightmap);
		bind(minInput, HeightmapCondition::getMin, HeightmapCondition::setMin);
		bind(maxInput, HeightmapCondition::getMax, HeightmapCondition::setMax);

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
