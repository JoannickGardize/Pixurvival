package com.pixurvival.contentPackEditor.component.mapGenerator;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.function.Supplier;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.HeightmapCondition;
import com.pixurvival.core.contentPack.map.TileGenerator;

public class TileGeneratorEditor extends ElementEditor<TileGenerator> {

	private static final long serialVersionUID = 1L;

	public TileGeneratorEditor(Supplier<Collection<Heightmap>> heightmapCollectionSupplier) {

		ListEditor<HeightmapCondition> heightmapConditionsEditor = new VerticalListEditor<>(LayoutUtils.bordered(() -> new HeightmapConditionEditor(heightmapCollectionSupplier)),
				HeightmapCondition::new, VerticalListEditor.VERTICAL);

		TileHashmapEditor tileHashmapEditor = new TileHashmapEditor(heightmapCollectionSupplier);

		// Binding

		bind(heightmapConditionsEditor, TileGenerator::getHeightmapConditions, TileGenerator::setHeightmapConditions);
		bind(tileHashmapEditor, TileGenerator::getTileHashmap, TileGenerator::setTileHashmap);

		// Layouting

		setLayout(new BorderLayout());
		heightmapConditionsEditor.setBorder(LayoutUtils.createGroupBorder("generic.conditions"));
		add(heightmapConditionsEditor, BorderLayout.CENTER);
		tileHashmapEditor.setBorder(LayoutUtils.createGroupBorder("tileGeneratorEditor.hash"));
		add(tileHashmapEditor, BorderLayout.SOUTH);
		LayoutUtils.setMinimumSize(heightmapConditionsEditor, 1, 120);
		// LayoutUtils.setMinimumSize(tileHashmapEditor, 1, 140);
	}
}
