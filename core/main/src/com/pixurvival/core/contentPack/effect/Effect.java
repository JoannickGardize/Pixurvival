package com.pixurvival.core.contentPack.effect;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Effect extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	private SpriteSheet spriteSheet;

	private OrientationType orientation;

	private boolean solid;

	private double duration;

	private double collisionRadius;

	private EffectMovement movement;

	private List<EffectTarget> effectTargets = new ArrayList<>();
}
