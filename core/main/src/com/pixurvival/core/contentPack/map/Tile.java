package com.pixurvival.core.contentPack.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.ImageReferenceHolder;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.ResourceReference;
import com.pixurvival.core.contentPack.validation.annotation.UnitSpriteFrame;
import com.pixurvival.core.contentPack.validation.annotation.UnitSpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tile extends NamedIdentifiedElement implements Serializable, ImageReferenceHolder {

	private static final long serialVersionUID = 1L;

	public static final byte SPECIAL_TILE = -1;

	private boolean solid = false;

	@Bounds(min = 0)
	private float velocityFactor = 1f;

	@UnitSpriteFrame
	@Valid
	@Length(min = 1)
	private List<Frame> frames = new ArrayList<>();

	@UnitSpriteSheet
	@ResourceReference
	private String image;
}
