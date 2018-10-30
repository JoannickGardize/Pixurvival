package com.pixurvival.core.contentPack.map;

import java.io.Serializable;

import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.ResourceReference;
import com.pixurvival.core.contentPack.sprite.Frame;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tile extends NamedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final byte SPECIAL_TILE = -1;

	private @Setter byte id;
	private boolean solid = false;
	private float velocityFactor = 1f;
	private Frame[] frames;
	@ResourceReference
	private String image;
}
