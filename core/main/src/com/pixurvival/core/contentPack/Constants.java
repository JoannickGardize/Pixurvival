package com.pixurvival.core.contentPack;

import java.io.Serializable;

import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Constants implements Serializable {

	private static final long serialVersionUID = 1L;

	@Required
	@ElementReference
	private SpriteSheet defaultCharacter;

	@Required
	@ElementReference
	private Tile outsideTile;

	@Bounds(min = 0)
	private double tileAnimationSpeed = 0.3;
}
