package com.pixurvival.core.contentPack.ai;

import java.io.Serializable;

import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ChangeCondition implements Serializable {

	private static final long serialVersionUID = 1L;

	private Behavior nextBehavior;

	public abstract boolean test(CreatureEntity creature);
}
