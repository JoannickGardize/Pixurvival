package com.pixurvival.core.alteration;

import com.pixurvival.core.Damageable;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.team.TeamMember;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
public class ContinuousDamageAlteration extends Alteration {

    private static final long serialVersionUID = 1L;

    @Valid
    private StatFormula damagePerSecond = new StatFormula();

    @Valid
    private DamageAttributes attributes = new DamageAttributes();

    @Override
    public void targetedApply(TeamMember source, TeamMember target) {
        if (target instanceof Damageable) {
            ((Damageable) target).takeDamage(damagePerSecond.getValue(source) * target.getWorld().getTime().getDeltaTime(), attributes);
        }
    }

    @Override
    public void forEachStatFormulas(Consumer<StatFormula> action) {
        action.accept(damagePerSecond);
    }
}
