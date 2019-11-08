package com.pixurvival.core.livingEntity.ability;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.alteration.Alteration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AlterationAbility extends CooldownAbility {

	private static final long serialVersionUID = 1L;

	private List<Alteration> alterations = new ArrayList<>();

	@Override
	public boolean fire(LivingEntity entity) {
		if (entity.getWorld().isServer()) {
			if (!canFire(entity)) {
				return false;
			}
			if (alterations != null) {
				alterations.forEach(a -> a.apply(entity, entity));
			}
			return true;
		} else {
			return false;
		}

	}

	public boolean canFire(LivingEntity entity) {
		return true;
	}

	public boolean isEmpty() {
		return alterations.isEmpty();
	}

}
