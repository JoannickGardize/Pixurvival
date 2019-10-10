package com.pixurvival.core.entity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.effect.DelayedFollowingElement;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.effect.EffectTarget;
import com.pixurvival.core.contentPack.effect.FollowingElement;
import com.pixurvival.core.contentPack.effect.OffsetAngleEffect;
import com.pixurvival.core.livingEntity.alteration.CheckListHolder;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

public class EffectEntity extends Entity implements CheckListHolder, TeamMember {

	private @Getter OffsetAngleEffect definition;

	private @Getter @Setter TeamMember ancestor;

	private @Getter @Setter Object movementData;

	private long creationTime;

	private int nextFollowingElementIndex = 0;

	private Collection<Object> checkList;
	private Object tmpChecked;

	private int numberOfDelayedFollowingElements;

	private long duration;

	public EffectEntity(OffsetAngleEffect definition, TeamMember ancestor) {
		this.definition = definition;
		this.ancestor = ancestor;
	}

	public EffectEntity() {
		definition = new OffsetAngleEffect();
	}

	@Override
	public void initialize() {
		if (getWorld().isServer()) {
			definition.getEffect().getMovement().initialize(this);
			creationTime = getWorld().getTime().getTimeMillis();
			List<DelayedFollowingElement> delayedFollowingElements = definition.getEffect().getDelayedFollowingElements();
			if (!delayedFollowingElements.isEmpty()) {
				int repeat = (int) definition.getEffect().getRepeatFollowingElements().getValue(this);
				numberOfDelayedFollowingElements = delayedFollowingElements.size() * repeat + 1;
				duration = Math.max(definition.getEffect().getDuration(), delayedFollowingElements.get(delayedFollowingElements.size() - 1).getDelay() * repeat);
			} else {
				duration = definition.getEffect().getDuration();
			}
		}
	}

	@Override
	public void update() {
		Effect effect = definition.getEffect();
		effect.getMovement().update(this);
		if (getWorld().isServer()) {
			long age = getWorld().getTime().getTimeMillis() - creationTime;
			if (age > duration || effect.isSolid() && getWorld().getMap().collide(this)) {
				setAlive(false);
			}
			processFollowingElements(age);
			processEffectTarget();
		}
		super.update();
	}

	private void processEffectTarget() {
		for (EffectTarget effectTarget : definition.getEffect().getTargets()) {
			EntitySearchUtils.forEach(this, effectTarget.getTargetType(), GameConstants.EFFECT_TARGET_DISTANCE_CHECK, e -> {
				if (collideDynamic(e)) {
					effectTarget.getAlterations().forEach(a -> a.apply(this, e));
					if (tmpChecked != null) {
						checkList.add(tmpChecked);
						tmpChecked = null;
					}
					if (effectTarget.isDestroyWhenCollide()) {
						setAlive(false);
					}
				}
			});
		}
	}

	private void processFollowingElements(long age) {
		Effect effect = definition.getEffect();
		if (nextFollowingElementIndex < numberOfDelayedFollowingElements) {
			DelayedFollowingElement nextDelayedFollowingElement;
			List<DelayedFollowingElement> followingElements = effect.getDelayedFollowingElements();
			int currentIndex = nextFollowingElementIndex % followingElements.size();
			int repeatCount = nextFollowingElementIndex / followingElements.size();
			long maxDelay = followingElements.get(followingElements.size() - 1).getDelay();
			while (nextFollowingElementIndex < numberOfDelayedFollowingElements && age >= (nextDelayedFollowingElement = followingElements.get(currentIndex)).getDelay() + repeatCount * maxDelay) {
				nextDelayedFollowingElement.getFollowingElement().apply(this);
				nextFollowingElementIndex++;
				currentIndex = nextFollowingElementIndex % followingElements.size();
				repeatCount = nextFollowingElementIndex / followingElements.size();
			}
		}
	}

	@Override
	protected void onDeath() {
		if (getWorld().isServer()) {
			for (FollowingElement followingElement : definition.getEffect().getDeathFollowingElements()) {
				followingElement.apply(this);
			}
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.EFFECT;
	}

	@Override
	public double getCollisionRadius() {
		return definition.getEffect().getCollisionRadius();
	}

	@Override
	public void writeInitialization(ByteBuffer buffer) {
		buffer.putShort((short) definition.getEffect().getId());
	}

	@Override
	public void applyInitialization(ByteBuffer buffer) {
		definition.setEffect(getWorld().getContentPack().getEffects().get(buffer.getShort()));
	}

	@Override
	public void writeUpdate(ByteBuffer buffer) {
		buffer.putDouble(getPosition().getX());
		buffer.putDouble(getPosition().getY());
		buffer.put(isForward() ? (byte) 1 : (byte) 0);
		buffer.putDouble(getMovingAngle());
		definition.getEffect().getMovement().writeUpdate(buffer, this);

	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		getPosition().set(buffer.getDouble(), buffer.getDouble());
		setForward(buffer.get() == 1);
		setMovingAngle(buffer.getDouble());
		definition.getEffect().getMovement().applyUpdate(buffer, this);
	}

	@Override
	public double getSpeedPotential() {
		return definition.getEffect().getMovement().getSpeedPotential(this);
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public boolean isChecked(Object object) {
		if (checkList == null) {
			checkList = new ArrayList<>(3);
			return false;
		} else {
			return checkList.contains(object);
		}
	}

	@Override
	public void check(Object object) {
		tmpChecked = object;
	}

	@Override
	public Vector2 getTargetPosition() {
		return Vector2.fromEuclidean(1, getMovingAngle()).add(getPosition());
	}

	@Override
	public Team getTeam() {
		return ancestor.getTeam();
	}

	@Override
	public StatSet getStats() {
		return ancestor.getStats();
	}

	@Override
	public TeamMember getOrigin() {
		return ancestor.getOrigin();
	}
}
