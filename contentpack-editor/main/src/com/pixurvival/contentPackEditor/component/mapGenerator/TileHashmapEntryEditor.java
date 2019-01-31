package com.pixurvival.contentPackEditor.component.mapGenerator;

import java.util.Collection;

import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.map.TileHashmapEntry;

public class TileHashmapEntryEditor extends ElementEditor<TileHashmapEntry> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Tile> tileChooser = new ElementChooserButton<>(IconService.getInstance()::get);
	private DoubleInput nextInput = new DoubleInput(new Bounds(0, 1));

	public TileHashmapEntryEditor() {

		bind(tileChooser, TileHashmapEntry::getTile, TileHashmapEntry::setTile);
		bind(nextInput, TileHashmapEntry::getNext, TileHashmapEntry::setNext);

		add(tileChooser);
		add(nextInput);
	}

	public void setTileCollection(Collection<Tile> collection) {
		tileChooser.setItems(collection);
	}
}
