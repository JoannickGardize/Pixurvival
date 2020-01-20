package com.pixurvival.core.contentPack.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.Dimensions;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.chunk.Chunk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Structure extends IdentifiedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean solid;

	@Required
	@ElementReference
	private SpriteSheet spriteSheet;

	@Valid
	@Required
	private Dimensions dimensions = new Dimensions(1, 1);

	private long duration = 0;

	private List<Tile> bannedTiles = new ArrayList<>();

	private float lightEmissionRadius = 0;

	private long deconstructionDuration;

	private transient Item deconstructionItem;

	public MapStructure newMapStructure(Chunk chunk, int x, int y) {
		return new MapStructure(chunk, this, x, y);
	}
}
