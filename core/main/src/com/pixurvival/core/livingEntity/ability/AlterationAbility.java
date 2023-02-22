package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.alteration.StatFormula;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.LivingEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Setter
public abstract class AlterationAbility extends CooldownAbility {

    private static final long serialVersionUID = 1L;

    @Valid
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

    @Override
    public void forEachAlteration(Consumer<Alteration> action) {
        alterations.forEach(action::accept);
    }
}
