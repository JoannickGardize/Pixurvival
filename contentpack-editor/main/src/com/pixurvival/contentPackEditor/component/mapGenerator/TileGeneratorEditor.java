package com.pixurvival.contentPackEditor.component.mapGenerator;

import java.awt.BorderLayout;
import java.util.Collection;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.HeightmapCondition;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.map.TileGenerator;

public class TileGeneratorEditor extends ElementEditor<TileGenerator> {

	private static final long serialVersionUID = 1L;

	private Collection<Heightmap> heightmapCollection = null;

	private ListEditor<HeightmapCondition> heightmapConditionsEditor = new VerticalListEditor<>(() -> {
		HeightmapConditionEditor result = new HeightmapConditionEditor();
		result.setBorder(LayoutUtils.createBorder());
		result.setHeightmapCollection(heightmapCollection);
		return result;
	}, HeightmapCondition::new, VerticalListEditor.HORIZONTAL);

	private TileHashmapEditor tileHashmapEditor = new TileHashmapEditor();

	public TileGeneratorEditor() {

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

	public void setHeightmapCollection(Collection<Heightmap> collection) {
		heightmapCollection = collection;
		heightmapConditionsEditor
				.forEachEditors(e -> ((HeightmapConditionEditor) e).setHeightmapCollection(collection));
		tileHashmapEditor.setHeightmapCollection(collection);
	}

	public void setTileCollection(Collection<Tile> tiles) {
		tileHashmapEditor.setTileCollection(tiles);
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {

	}

	// @Override
	// protected void valueChanged(ValueComponent<?> source) {
	// LayoutUtils.setMinimumSize(this, 1, (int)
	// getLayout().preferredLayoutSize(this).getHeight());
	// }
}
