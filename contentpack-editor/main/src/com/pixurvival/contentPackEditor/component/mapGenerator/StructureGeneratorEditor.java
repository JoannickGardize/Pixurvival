package com.pixurvival.contentPackEditor.component.mapGenerator;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.function.Supplier;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.WeightedValueProducerEditor;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.HeightmapCondition;
import com.pixurvival.core.contentPack.map.StructureGenerator;
import com.pixurvival.core.contentPack.structure.Structure;

public class StructureGeneratorEditor extends ElementEditor<StructureGenerator> {

	private static final long serialVersionUID = 1L;

	public StructureGeneratorEditor(Supplier<Collection<Heightmap>> heightmapCollectionSupplier) {

		ListEditor<HeightmapCondition> heightmapConditionsEditor = new VerticalListEditor<>(() -> {
			HeightmapConditionEditor result = new HeightmapConditionEditor(heightmapCollectionSupplier);
			result.setBorder(LayoutUtils.createBorder());
			return result;
		}, HeightmapCondition::new, VerticalListEditor.HORIZONTAL);

		WeightedValueProducerEditor<Structure> structureProducerEditor = new WeightedValueProducerEditor<>(Structure.class);
		DoubleInput densityInput = new DoubleInput(new Bounds(0, 1));

		// Binding

		bind(densityInput, StructureGenerator::getDensity, StructureGenerator::setDensity);
		bind(heightmapConditionsEditor, StructureGenerator::getHeightmapConditions, StructureGenerator::setHeightmapConditions);
		bind(structureProducerEditor, StructureGenerator::getStructureProducer, StructureGenerator::setStructureProducer);

		// Layouting

		setLayout(new BorderLayout());
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel wrapper = new JPanel();
		wrapper.add(LayoutUtils.labelled("structureGeneratorEditor.density", densityInput));
		heightmapConditionsEditor.setBorder(LayoutUtils.createGroupBorder("generic.conditions"));
		topPanel.add(wrapper, BorderLayout.EAST);
		topPanel.add(heightmapConditionsEditor, BorderLayout.CENTER);
		add(topPanel, BorderLayout.CENTER);
		structureProducerEditor.setBorder(LayoutUtils.createGroupBorder("elementType.structure"));
		add(structureProducerEditor, BorderLayout.SOUTH);
		LayoutUtils.setMinimumSize(heightmapConditionsEditor, 1, 120);
		// LayoutUtils.setMinimumSize(tileHashmapEditor, 1, 140);
	}
}
