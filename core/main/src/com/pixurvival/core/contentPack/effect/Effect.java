package com.pixurvival.core.contentPack.effect;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Effect extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	@ElementReference
	private SpriteSheet spriteSheet;

	@Required
	private OrientationType orientation;

	private boolean solid;

	@Bounds(min = 0)
	private double duration;

	@Bounds(min = 0)
	private double collisionRadius;

	@Required
	@Valid
	private EffectMovement movement;

	@Valid
	@Required
	private List<EffectTarget> targets = new ArrayList<>();
}
