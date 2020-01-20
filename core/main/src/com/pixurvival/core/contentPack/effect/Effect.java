package com.pixurvival.core.contentPack.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.InstantDamageAlteration;
import com.pixurvival.core.livingEntity.alteration.StatFormula;
import com.pixurvival.core.map.DamageableMapStructure;
import com.pixurvival.core.map.MapTile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Effect extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	private transient InstantDamageAlteration structureDamageAlteration;

	private transient BiConsumer<EffectEntity, MapTile> tileCollisionAction;

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
	private float entityCollisionRadius;

	private float mapCollisionRadius;

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

	private List<Alteration> deathAlterations = new ArrayList<>();

	@Override
	public void forEachStatFormulas(Consumer<StatFormula> action) {
		action.accept(repeatFollowingElements);
		targets.forEach(t -> t.getAlterations().forEach(a -> a.forEachStatFormulas(action)));
		deathAlterations.forEach(a -> a.forEachStatFormulas(action));
	}

	@Override
	public void initialize() {
		initializeStructureDamage();
		if (solid) {
			if (structureDamageAlteration != null) {
				tileCollisionAction = (entity, tile) -> {
					if (tile.isSolid()) {
						entity.setAlive(false);
						entity.setSneakyDeath(true);
					}
					if (tile.getStructure() instanceof DamageableMapStructure) {
						structureDamageAlteration.apply(entity, ((DamageableMapStructure) tile.getStructure()));
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
		} else if (structureDamageAlteration != null) {
			tileCollisionAction = (entity, tile) -> {
				if (tile.getStructure() instanceof DamageableMapStructure) {
					structureDamageAlteration.apply(entity, ((DamageableMapStructure) tile.getStructure()));
					entity.flushCheckList();
				}
			};
		}
	}

	public void initializeStructureDamage() {
		for (EffectTarget effectTarget : targets) {
			if (effectTarget.getTargetType() == TargetType.ALL_ENEMIES) {
				for (Alteration alteration : effectTarget.getAlterations()) {
					if (alteration instanceof InstantDamageAlteration) {
						structureDamageAlteration = (InstantDamageAlteration) alteration;
						return;
					}
				}
			}
		}
	}
}
