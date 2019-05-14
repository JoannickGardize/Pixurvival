package com.pixurvival.contentPackEditor.component.mapGenerator;

import java.awt.BorderLayout;
import java.util.Collection;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.HorizontalListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.map.TileHashmap;
import com.pixurvival.core.contentPack.map.TileHashmapEntry;

public class TileHashmapEditor extends ElementEditor<TileHashmap> {

	private static final long serialVersionUID = 1L;

	private Collection<Tile> tileCollection;
	private ElementChooserButton<Heightmap> heightmapChooser = new ElementChooserButton<>();
	private ListEditor<TileHashmapEntry> entriesEditor = new HorizontalListEditor<>(() -> {
		TileHashmapEntryEditor editor = new TileHashmapEntryEditor();
		editor.setBorder(LayoutUtils.createBorder());
		editor.setTileCollection(tileCollection);
		return editor;
	}, TileHashmapEntry::new);

	public TileHashmapEditor() {
		bind(heightmapChooser, TileHashmap::getHeightmap, TileHashmap::setHeightmap);
		bind(entriesEditor, TileHashmap::getEntries, TileHashmap::setEntries);

		setLayout(new BorderLayout());
		add(LayoutUtils.labelled("mapGeneratorEditor.heightmap", heightmapChooser), BorderLayout.NORTH);
		add(entriesEditor, BorderLayout.CENTER);
	}

	public void setHeightmapCollection(Collection<Heightmap> collection) {
		heightmapChooser.setItems(collection);
	}

	public void setTileCollection(Collection<Tile> collection) {
		tileCollection = collection;
		entriesEditor.forEachEditors(e -> ((TileHashmapEntryEditor) e).setTileCollection(collection));
	}
}
