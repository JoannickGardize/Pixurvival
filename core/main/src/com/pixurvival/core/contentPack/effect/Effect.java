package com.pixurvival.core.contentPack.effect;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class Effect {

	private SpriteSheet spriteSheet;

	private EffectMovement movement;

	private boolean solid;

	private double maxDuration;

	private List<EffectTarget> effectTargets = new ArrayList<>();
}
