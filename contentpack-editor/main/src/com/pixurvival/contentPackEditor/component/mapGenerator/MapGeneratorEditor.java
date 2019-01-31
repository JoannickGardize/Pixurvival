package com.pixurvival.contentPackEditor.component.mapGenerator;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.TileGenerator;

public class MapGeneratorEditor extends RootElementEditor<MapGenerator> {

	private static final long serialVersionUID = 1L;

	private ListEditor<TileGenerator> tileGeneratorsEditor = new VerticalListEditor<>(() -> {
		TileGeneratorEditor editor = new TileGeneratorEditor();
		ContentPack currentPack = FileService.getInstance().getCurrentContentPack();
		if (currentPack != null) {
			editor.setTileCollection(currentPack.getTiles());
		}
		if (getValue() != null) {
			editor.setHeightmapCollection(getValue().getHeightmaps());
		}
		editor.setBorder(LayoutUtils.createBorder());
		return editor;
	}, TileGenerator::new);

	public MapGeneratorEditor() {

		// Construction
		VerticalListEditor<Heightmap> heightmapsEditor = new VerticalListEditor<>(() -> {
			HeightmapEditor result = new HeightmapEditor();
			result.setBorder(LayoutUtils.createBorder());
			return result;
		}, Heightmap::new);

		// Binding

		bind(heightmapsEditor, MapGenerator::getHeightmaps, MapGenerator::setHeightmaps);
		bind(tileGeneratorsEditor, MapGenerator::getTileGenerators, MapGenerator::setTileGenerators);

		// Layouting
		setLayout(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);
		TranslationService t = TranslationService.getInstance();
		tabbedPane.addTab(t.getString("mapGeneratorEditor.heightmap"), heightmapsEditor);
		tabbedPane.addTab(t.getString("mapGeneratorEditor.tileGenerator"), tileGeneratorsEditor);
	}

	// @Override
	// protected void valueChanged(ValueComponent<?> source) {
	// if (source == this) {
	// tileGeneratorsEditor.forEachEditors(e -> ((TileGeneratorEditor) e)
	// .setHeightmapCollection(((MapGenerator)
	// source.getValue()).getHeightmaps()));
	// }
	// }
}
