package com.pixurvival.core.livingEntity.ability;

import java.util.List;

import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.alteration.Alteration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EffectAbility extends CooldownAbility {

	private static final long serialVersionUID = 1L;

	private List<Alteration> selfAlterations;
	private List<Effect> effects;

	@Override
	public void fire(LivingEntity entity) {
		if (entity.getWorld().isServer()) {
			if (selfAlterations != null) {
				selfAlterations.forEach(a -> a.apply(this, entity));
			}
			for (Effect effect : effects) {
				EffectEntity effectEntity = new EffectEntity(effect, entity);
				entity.getWorld().getEntityPool().add(effectEntity);
			}
		}
	}
}
