package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.entity.EffectEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowingEffect extends FollowingElement {

	private static final long serialVersionUID = 1L;

	private OffsetAngleEffect offsetAngleEffect;

	@Override
	public void apply(EffectEntity effectEntity) {
		EffectEntity following = new EffectEntity(offsetAngleEffect, effectEntity);
		effectEntity.getWorld().getEntityPool().add(following);
	}

}
