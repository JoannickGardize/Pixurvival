package com.pixurvival.core.contentPack.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.StatFormula;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Effect extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	@ElementReference
	private SpriteSheet spriteSheet;

	@Required
	private OrientationType orientation = OrientationType.STATIC;

	private boolean loopAnimation = true;

	private DrawDepth drawDepth = DrawDepth.NORMAL;

	private boolean solid;

	@Bounds(min = 0)
	private long duration;

	@Bounds(min = 0)
	private float collisionRadius;

	@Required
	@Valid
	private EffectMovement movement;

	@Valid
	@Required
	private List<EffectTarget> targets = new ArrayList<>();

	/**
	 * Number of loop to do of delayedFollowingElements list
	 */
	private StatFormula repeatFollowingElements = new StatFormula();

	/**
	 * Must be ascending by delay.
	 */
	private List<DelayedFollowingElement> delayedFollowingElements = new ArrayList<>();

	// TODO Fusionner avec deathAlterations
	private List<FollowingElement> deathFollowingElements = new ArrayList<>();

	private List<Alteration> deathAlterations = new ArrayList<>();

	@Override
	public void forEachStatFormulas(Consumer<StatFormula> action) {
		action.accept(repeatFollowingElements);
		targets.forEach(t -> t.getAlterations().forEach(a -> a.forEachStatFormulas(action)));
		deathAlterations.forEach(a -> a.forEachStatFormulas(action));
	}
}
