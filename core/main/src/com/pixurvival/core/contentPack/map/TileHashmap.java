package com.pixurvival.core.contentPack.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.validation.annotation.Ascending;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileHashmap implements Serializable {

	private static final long serialVersionUID = 1L;

	@ElementReference("<<<.heightmaps")
	private Heightmap heightmap;

	@Valid
	@Length(min = 1)
	@Ascending
	private List<TileHashmapEntry> entries = new ArrayList<>();

	public Tile get(int x, int y) {
		// TODO Cache result temporary during chunk generation?
		float noise = heightmap.getNoise(x, y);
		for (TileHashmapEntry entry : entries) {
			if (noise < entry.getNext()) {
				return entry.getTile();
			}
		}
		return entries.get(entries.size() - 1).getTile();
	}
}
