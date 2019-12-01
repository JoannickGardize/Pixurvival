package com.pixurvival.contentPackEditor.component.mapGenerator;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.map.TileHashmapEntry;

public class TileHashmapEntryEditor extends ElementEditor<TileHashmapEntry> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Tile> tileChooser = new ElementChooserButton<>(Tile.class);
	private FloatInput nextInput = new FloatInput(new Bounds(0, 1));

	public TileHashmapEntryEditor() {

		bind(tileChooser, TileHashmapEntry::getTile, TileHashmapEntry::setTile);
		bind(nextInput, TileHashmapEntry::getNext, TileHashmapEntry::setNext);

		add(tileChooser);
		add(nextInput);
	}

}
