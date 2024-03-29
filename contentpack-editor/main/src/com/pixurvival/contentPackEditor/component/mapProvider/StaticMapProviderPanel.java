package com.pixurvival.contentPackEditor.component.mapProvider;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ResourceFileChooser;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.map.StaticMapProvider;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.structure.Structure;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class StaticMapProviderPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public StaticMapProviderPanel(MapProviderEditor mapProviderEditor) {

        ElementChooserButton<Tile> defaultTileChooser = new ElementChooserButton<>(Tile.class);

        mapProviderEditor.bind(defaultTileChooser, "defaultTile", StaticMapProvider.class);

        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);
        TranslationService t = TranslationService.getInstance();

        JPanel tilePanel = new JPanel(new BorderLayout());
        tilePanel.add(LayoutUtils.single(LayoutUtils.labelled("mapGeneratorEditor.defaultTile", defaultTileChooser)), BorderLayout.NORTH);
        tilePanel.add(createMappingTab(mapProviderEditor, Tile.class,
                () -> mapProviderEditor.getValue() instanceof StaticMapProvider ? ((StaticMapProvider) mapProviderEditor.getValue()).getTilesImageResourceName() : null), BorderLayout.CENTER);
        tabbedPane.addTab(t.getString("elementType.tile"), tilePanel);
        tabbedPane.addTab(t.getString("elementType.structure"), createMappingTab(mapProviderEditor, Structure.class,
                () -> mapProviderEditor.getValue() instanceof StaticMapProvider ? ((StaticMapProvider) mapProviderEditor.getValue()).getStructuresImageResourceName() : null));
    }

    @SuppressWarnings("unchecked")
    public <T extends NamedIdentifiedElement> JPanel createMappingTab(MapProviderEditor mapProviderEditor, Class<T> type, Supplier<String> imageResourceNameSupplier) {

        ResourceFileChooser resourceFileChooser = new ResourceFileChooser();

        JPanel panel = new JPanel(new BorderLayout(LayoutUtils.DEFAULT_GAP, LayoutUtils.DEFAULT_GAP));
        ImageMappingEditor<T> imageMappingEditor = new ImageMappingEditor<>(type);

        if (type == Tile.class) {
            mapProviderEditor.bind((ImageMappingEditor<Tile>) imageMappingEditor, "tileMap", StaticMapProvider.class);
            mapProviderEditor.bind(resourceFileChooser, "tilesImageResourceName", StaticMapProvider.class, false).getter(StaticMapProvider::getTilesImageResourceName).setter((m, s) -> {
            });
        } else {
            mapProviderEditor.bind((ImageMappingEditor<Structure>) imageMappingEditor, "structureMap", StaticMapProvider.class);
            mapProviderEditor.bind(resourceFileChooser, "structuresImageResourceName", StaticMapProvider.class, false).getter(StaticMapProvider::getStructuresImageResourceName).setter((m, s) -> {
            });
        }
        resourceFileChooser.setBorder(LayoutUtils.createBorder());
        imageMappingEditor.setBorder(LayoutUtils.createBorder());

        panel.add(resourceFileChooser, BorderLayout.NORTH);
        panel.add(imageMappingEditor, BorderLayout.CENTER);
        return panel;
    }
}
