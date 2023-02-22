package com.pixurvival.contentPackEditor.component.mapProvider;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.HorizontalListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.TileHashmap;
import com.pixurvival.core.contentPack.map.TileHashmapEntry;

import java.awt.*;
import java.util.Collection;
import java.util.function.Supplier;

public class TileHashmapEditor extends ElementEditor<TileHashmap> {

    private static final long serialVersionUID = 1L;

    public TileHashmapEditor(Supplier<Collection<Heightmap>> heightmapCollectionSupplier) {
        super(TileHashmap.class);

        ElementChooserButton<Heightmap> heightmapChooser = new ElementChooserButton<>(heightmapCollectionSupplier);

        ListEditor<TileHashmapEntry> entriesEditor = new HorizontalListEditor<>(LayoutUtils.bordered(TileHashmapEntryEditor::new), TileHashmapEntry::new);

        bind(heightmapChooser, "heightmap");
        bind(entriesEditor, "entries");

        setLayout(new BorderLayout());
        add(LayoutUtils.labelled("mapGeneratorEditor.heightmap", heightmapChooser), BorderLayout.NORTH);
        add(entriesEditor, BorderLayout.CENTER);
    }
}
