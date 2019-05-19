package com.pixurvival.contentPackEditor.component.mapGenerator;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.HorizontalListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.WeightedValueProducerEditor;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.HeightmapCondition;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.StructureGenerator;
import com.pixurvival.core.contentPack.map.Tile;

public class StructureGeneratorEditor extends ElementEditor<StructureGenerator> {

	private static final long serialVersionUID = 1L;

	private Collection<Heightmap> heightmapCollection;

	private ListEditor<HeightmapCondition> heightmapConditionsEditor = new VerticalListEditor<>(() -> {
		HeightmapConditionEditor result = new HeightmapConditionEditor();
		result.setHeightmapCollection(heightmapCollection);
		result.setBorder(LayoutUtils.createBorder());
		return result;
	}, HeightmapCondition::new, VerticalListEditor.HORIZONTAL);

	private ListEditor<Tile> bannedTilesEditor = new HorizontalListEditor<>(() -> {
		ElementChooserButton<Tile> tileChooser = new ElementChooserButton<>(IconService.getInstance()::get, true);
		tileChooser.setBorder(LayoutUtils.createBorder());
		ContentPack currentPack = FileService.getInstance().getCurrentContentPack();
		if (currentPack != null) {
			tileChooser.setItems(currentPack.getTiles());
		}
		return tileChooser;
	}, () -> null);

	private WeightedValueProducerEditor<Structure> structureProducerEditor = new WeightedValueProducerEditor<>(IconService.getInstance()::get, ContentPack::getStructures);

	public StructureGeneratorEditor() {

		DoubleInput densityInput = new DoubleInput(new Bounds(0, 1));

		// Binding

		bind(densityInput, StructureGenerator::getDensity, StructureGenerator::setDensity);
		bind(heightmapConditionsEditor, StructureGenerator::getHeightmapConditions, StructureGenerator::setHeightmapConditions);
		bind(bannedTilesEditor, StructureGenerator::getBannedTiles, StructureGenerator::setBannedTiles);
		bind(structureProducerEditor, StructureGenerator::getStructureProducer, StructureGenerator::setStructureProducer);

		// Layouting

		setLayout(new BorderLayout());
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel wrapper = new JPanel();
		wrapper.add(LayoutUtils.labelled("structureGeneratorEditor.density", densityInput));
		heightmapConditionsEditor.setBorder(LayoutUtils.createGroupBorder("generic.conditions"));
		bannedTilesEditor.setBorder(LayoutUtils.createGroupBorder("structureGeneratorEditor.bannedTiles"));
		topPanel.add(LayoutUtils.createVerticalBox(heightmapConditionsEditor, bannedTilesEditor), BorderLayout.CENTER);
		topPanel.add(wrapper, BorderLayout.EAST);
		add(topPanel, BorderLayout.CENTER);
		structureProducerEditor.setBorder(LayoutUtils.createGroupBorder("elementType.structure"));
		add(structureProducerEditor, BorderLayout.SOUTH);
		LayoutUtils.setMinimumSize(heightmapConditionsEditor, 1, 120);
		// LayoutUtils.setMinimumSize(tileHashmapEditor, 1, 140);
	}

	public void setHeightmapCollection(Collection<Heightmap> collection) {
		heightmapCollection = collection;
		heightmapConditionsEditor.forEachEditors(e -> ((HeightmapConditionEditor) e).setHeightmapCollection(collection));
	}

	public void setStructureCollection(Collection<Structure> structures) {
		structureProducerEditor.setAllItems(structures);
	}

	// @Override
	// protected void valueChanged(ValueComponent<?> source) {
	// LayoutUtils.setMinimumSize(this, 1, (int)
	// getLayout().preferredLayoutSize(this).getHeight());
	// }
}
