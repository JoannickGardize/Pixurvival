package com.pixurvival.core.livingEntity.ability;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.StatFormula;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AlterationAbility extends CooldownAbility {

	private static final long serialVersionUID = 1L;

	private List<Alteration> alterations = new ArrayList<>();

	@Override
	public boolean fire(LivingEntity entity) {
		if (!canFire(entity)) {
			return false;
		}
		if (entity.getWorld().isServer() && alterations != null) {
			alterations.forEach(a -> a.apply(entity, entity));
		}
		return true;

	}

	public boolean canFire(LivingEntity entity) {
		return true;
	}

	public boolean isEmpty() {
		return alterations.isEmpty();
	}

	@Override
	public void forEachStatFormulas(Consumer<StatFormula> action) {
		alterations.forEach(a -> a.forEachStatFormulas(action));
	}

}
