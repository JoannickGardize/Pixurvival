package com.pixurvival.contentPackEditor.component.mapProvider;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.map.*;

import javax.swing.*;
import java.awt.*;

public class ProcedurallyGeneratedMapProviderPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public ProcedurallyGeneratedMapProviderPanel(MapProviderEditor mapProviderEditor) {
        // Construction

        ElementChooserButton<Tile> defaultTileChooser = new ElementChooserButton<>(Tile.class);

        ListEditor<TileGenerator> tileGeneratorsEditor = new VerticalListEditor<>(() -> {
            TileGeneratorEditor editor = new TileGeneratorEditor(mapProviderEditor.getHeightmapCollectionSupplier());
            editor.setBorder(LayoutUtils.createBorder());
            return editor;
        }, TileGenerator::new);

        ListEditor<StructureGenerator> structureGeneratorsEditor = new VerticalListEditor<>(() -> {
            StructureGeneratorEditor editor = new StructureGeneratorEditor(mapProviderEditor.getHeightmapCollectionSupplier());
            editor.setBorder(LayoutUtils.createBorder());
            return editor;
        }, StructureGenerator::new);

        VerticalListEditor<Heightmap> heightmapsEditor = new VerticalListEditor<>(() -> {
            HeightmapEditor result = new HeightmapEditor();
            result.setBorder(LayoutUtils.createBorder());
            return result;
        }, Heightmap::new);

        // Binding

        mapProviderEditor.bind(heightmapsEditor, "heightmaps", ProcedurallyGeneratedMapProvider.class);
        mapProviderEditor.bind(tileGeneratorsEditor, "tileGenerators", ProcedurallyGeneratedMapProvider.class);
        mapProviderEditor.bind(defaultTileChooser, "defaultTile", ProcedurallyGeneratedMapProvider.class);
        mapProviderEditor.bind(structureGeneratorsEditor, "structureGenerators",
                ProcedurallyGeneratedMapProvider.class);

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
}
