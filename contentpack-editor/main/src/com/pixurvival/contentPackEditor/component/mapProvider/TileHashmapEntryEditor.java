package com.pixurvival.contentPackEditor.component.mapProvider;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.map.TileHashmapEntry;

public class TileHashmapEntryEditor extends ElementEditor<TileHashmapEntry> {

    private static final long serialVersionUID = 1L;

    public TileHashmapEntryEditor() {
        super(TileHashmapEntry.class);

        ElementChooserButton<Tile> tileChooser = new ElementChooserButton<>(Tile.class);
        FloatInput nextInput = new FloatInput();

        bind(tileChooser, "tile");
        bind(nextInput, "next");

        add(tileChooser);
        add(nextInput);
    }

}
