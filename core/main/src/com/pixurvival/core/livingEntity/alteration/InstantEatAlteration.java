package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.entity.SourceProvider;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstantEatAlteration implements Alteration {

	private static final long serialVersionUID = 1L;

	private StatAmount amount = new StatAmount();

	@Override
	public void apply(SourceProvider source, LivingEntity entity) {
		if (entity instanceof PlayerEntity) {
			((PlayerEntity) entity).addHunger(amount.getValue(source));
		}
	}

}
