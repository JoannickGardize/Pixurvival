package com.pixurvival.core.contentPack.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.alteration.InstantDamageAlteration;
import com.pixurvival.core.alteration.InstantHealAlteration;
import com.pixurvival.core.alteration.StatFormula;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirement;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirementSet;
import com.pixurvival.core.contentPack.validation.annotation.Ascending;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Nullable;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.map.MapTile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Effect extends NamedIdentifiedElement {

	private static final long serialVersionUID = 1L;

	private transient List<Alteration> structureAlterations;

	private transient BiConsumer<EffectEntity, MapTile> tileCollisionAction;

	@Nullable
	@ElementReference
	@AnimationTemplateRequirement(AnimationTemplateRequirementSet.DEFAULT_OR_BEFORE_DEFAULT)
	private SpriteSheet spriteSheet;

	private OrientationType orientation = OrientationType.STATIC;

	private DrawDepth drawDepth = DrawDepth.NORMAL;

	private boolean solid;

	@Positive
	private long duration;

	@Positive
	private float entityCollisionRadius;

	@Positive
	private float mapCollisionRadius;

	@Valid
	private EffectMovement movement;

	@Valid
	private List<EffectTarget> targets = new ArrayList<>();

	/**
	 * Number of loop to do of delayedFollowingElements list
	 */
	private StatFormula repeatFollowingElements = new StatFormula();

	/**
	 * Must be ascending by delay.
	 */
	@Valid
	@Ascending
	private List<DelayedFollowingElement> delayedFollowingElements = new ArrayList<>();

	@Valid
	private List<Alteration> deathAlterations = new ArrayList<>();

	@Override
	public void forEachStatFormula(Consumer<StatFormula> action) {
		action.accept(repeatFollowingElements);
		targets.forEach(t -> t.getAlterations().forEach(a -> a.forEachStatFormulas(action)));
		deathAlterations.forEach(a -> a.forEachStatFormulas(action));
	}

	@Override
	public void forEachAlteration(Consumer<Alteration> action) {
		targets.forEach(t -> t.getAlterations().forEach(action::accept));
		deathAlterations.forEach(action::accept);
	}

	@Override
	public void initialize() {
		initializeStructureAlterations();
		if (solid) {
			if (!structureAlterations.isEmpty()) {
				tileCollisionAction = (entity, tile) -> {
					if (tile.isSolid()) {
						entity.setAlive(false);
						entity.setSneakyDeath(true);
					}
					if (tile.getStructure() != null) {
						structureAlterations.forEach(a -> a.apply(entity, tile.getStructure()));
						entity.flushCheckList();
					}
				};
			} else {
				tileCollisionAction = (entity, tile) -> {
					if (tile.isSolid()) {
						entity.setAlive(false);
						entity.setSneakyDeath(true);
					}
				};
			}
		} else if (!structureAlterations.isEmpty()) {
			tileCollisionAction = (entity, tile) -> {
				if (tile.getStructure() != null) {
					structureAlterations.forEach(a -> a.apply(entity, tile.getStructure()));
					entity.flushCheckList();
				}
			};
		}
	}

	private void initializeStructureAlterations() {
		structureAlterations = new ArrayList<>();
		for (EffectTarget effectTarget : targets) {
			if (effectTarget.getTargetType() == TargetType.ALL_ENEMIES) {
				for (Alteration alteration : effectTarget.getAlterations()) {
					if (alteration instanceof InstantDamageAlteration && ((InstantDamageAlteration) alteration).isApplyToStructures()) {
						structureAlterations.add(alteration);
						return;
					}
				}
			}
			if (effectTarget.getTargetType() == TargetType.STRUCTURES) {
				for (Alteration alteration : effectTarget.getAlterations()) {
					if (alteration instanceof InstantHealAlteration) {
						structureAlterations.add(alteration);
						return;
					}
				}
			}
		}
	}
}
