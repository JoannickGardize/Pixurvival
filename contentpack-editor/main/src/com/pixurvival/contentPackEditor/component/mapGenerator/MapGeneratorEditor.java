package com.pixurvival.contentPackEditor.component.mapGenerator;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.ProcedurallyGeneratedMapProvider;
import com.pixurvival.core.contentPack.map.StructureGenerator;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.map.TileGenerator;

public class MapGeneratorEditor extends RootElementEditor<ProcedurallyGeneratedMapProvider> {

	private static final long serialVersionUID = 1L;

	public MapGeneratorEditor() {

		// Construction

		ElementChooserButton<Tile> defaultTileChooser = new ElementChooserButton<>(Tile.class);

		ListEditor<TileGenerator> tileGeneratorsEditor = new VerticalListEditor<>(() -> {
			TileGeneratorEditor editor = new TileGeneratorEditor(getHeightmapCollectionSupplier());
			editor.setBorder(LayoutUtils.createBorder());
			return editor;
		}, TileGenerator::new);

		ListEditor<StructureGenerator> structureGeneratorsEditor = new VerticalListEditor<>(() -> {
			StructureGeneratorEditor editor = new StructureGeneratorEditor(getHeightmapCollectionSupplier());
			editor.setBorder(LayoutUtils.createBorder());
			return editor;
		}, StructureGenerator::new);

		VerticalListEditor<Heightmap> heightmapsEditor = new VerticalListEditor<>(() -> {
			HeightmapEditor result = new HeightmapEditor();
			result.setBorder(LayoutUtils.createBorder());
			return result;
		}, Heightmap::new);

		// Binding

		bind(heightmapsEditor, ProcedurallyGeneratedMapProvider::getHeightmaps, ProcedurallyGeneratedMapProvider::setHeightmaps);
		bind(tileGeneratorsEditor, ProcedurallyGeneratedMapProvider::getTileGenerators, ProcedurallyGeneratedMapProvider::setTileGenerators);
		bind(defaultTileChooser, ProcedurallyGeneratedMapProvider::getDefaultTile, ProcedurallyGeneratedMapProvider::setDefaultTile);
		bind(structureGeneratorsEditor, ProcedurallyGeneratedMapProvider::getStructureGenerators, ProcedurallyGeneratedMapProvider::setStructureGenerators);

		// Layouting

		JPanel tileGeneratorTab = new JPanel(new BorderLayout());
		tileGeneratorTab.add(LayoutUtils.labelled("mapGeneratorEditor.defaultTile", defaultTileChooser), BorderLayout.NORTH);
		tileGeneratorTab.add(tileGeneratorsEditor, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);
		TranslationService t = TranslationService.getInstance();
		tabbedPane.addTab(t.getString("mapGeneratorEditor.heightmap"), heightmapsEditor);
		tabbedPane.addTab(t.getString("mapGeneratorEditor.tileGenerator"), tileGeneratorTab);
		tabbedPane.addTab(t.getString("mapGeneratorEditor.structureGenerator"), structureGeneratorsEditor);
	}

	@Override
	public boolean isValueValid(ProcedurallyGeneratedMapProvider value) {
		if (value == null) {
			return false;
		}
		ProcedurallyGeneratedMapProvider previousValue = getValue();
		setValue(value);
		boolean result = super.isValueValid(value);
		setValue(previousValue);
		return result;
	}

	private Supplier<Collection<Heightmap>> getHeightmapCollectionSupplier() {
		return () -> {
			if (getValue() == null) {
				return Collections.emptyList();
			} else {
				return getValue().getHeightmaps();
			}
		};
	}
}
