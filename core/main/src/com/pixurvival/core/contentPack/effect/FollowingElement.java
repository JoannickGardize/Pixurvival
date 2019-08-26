package com.pixurvival.core.contentPack.effect;

import java.io.Serializable;

import com.pixurvival.core.entity.EffectEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class FollowingElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract void apply(EffectEntity effectEntity);
}