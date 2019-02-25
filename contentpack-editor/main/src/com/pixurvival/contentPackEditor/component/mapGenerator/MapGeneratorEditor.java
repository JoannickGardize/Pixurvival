package com.pixurvival.contentPackEditor.component.mapGenerator;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.StructureGenerator;
import com.pixurvival.core.contentPack.map.Tile;
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

	private ListEditor<StructureGenerator> structureGeneratorsEditor = new VerticalListEditor<>(() -> {
		StructureGeneratorEditor editor = new StructureGeneratorEditor();
		ContentPack currentPack = FileService.getInstance().getCurrentContentPack();
		if (currentPack != null) {
			editor.setStructureCollection(currentPack.getStructures());
		}
		if (getValue() != null) {
			editor.setHeightmapCollection(getValue().getHeightmaps());
		}
		editor.setBorder(LayoutUtils.createBorder());
		return editor;
	}, StructureGenerator::new);

	private ElementChooserButton<Tile> defaultTileChooser = new ElementChooserButton<>(IconService.getInstance()::get);

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
		bind(defaultTileChooser, MapGenerator::getDefaultTile, MapGenerator::setDefaultTile);
		bind(structureGeneratorsEditor, MapGenerator::getStructureGenerators, MapGenerator::setStructureGenerators);

		// Layouting

		JPanel tileGeneratorTab = new JPanel(new BorderLayout());
		tileGeneratorTab.add(LayoutUtils.labelled("mapGeneratorEditor.defaultTile", defaultTileChooser),
				BorderLayout.NORTH);
		tileGeneratorTab.add(tileGeneratorsEditor, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);
		TranslationService t = TranslationService.getInstance();
		tabbedPane.addTab(t.getString("mapGeneratorEditor.heightmap"), heightmapsEditor);
		tabbedPane.addTab(t.getString("mapGeneratorEditor.tileGenerator"), tileGeneratorTab);
		tabbedPane.addTab(t.getString("mapGeneratorEditor.structureGenerator"), structureGeneratorsEditor);
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		// tileGeneratorsEditor
		// .forEachEditors(e -> ((TileGeneratorEditor)
		// e).setTileCollection(event.getContentPack().getTiles()));
		defaultTileChooser.setItems(event.getContentPack().getTiles());
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		if (source == this) {
			tileGeneratorsEditor.forEachEditors(e -> ((TileGeneratorEditor) e)
					.setHeightmapCollection(((MapGenerator) source.getValue()).getHeightmaps()));
		}
	}
}
