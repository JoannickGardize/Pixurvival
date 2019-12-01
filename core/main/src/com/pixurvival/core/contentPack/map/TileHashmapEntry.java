package com.pixurvival.core.contentPack.map;

import java.io.Serializable;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileHashmapEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	@Required
	@ElementReference
	private Tile tile;

	@Bounds(min = 0, max = 1)
	private float next;
}
