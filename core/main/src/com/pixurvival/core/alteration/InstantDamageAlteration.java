package com.pixurvival.core.alteration;

import com.pixurvival.core.Damageable;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.team.TeamMember;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
public class InstantDamageAlteration extends UniqueAlteration {

    private static final long serialVersionUID = 1L;

    @Valid
    private StatFormula amount = new StatFormula();

    private boolean applyToStructures = true;

    @Valid
    private DamageAttributes attributes = new DamageAttributes();

    @Override
    public void uniqueApply(TeamMember source, TeamMember target) {
        if (target instanceof Damageable) {
            ((Damageable) target).takeDamage(amount.getValue(source), attributes);
        }
    }

    @Override
    public void forEachStatFormulas(Consumer<StatFormula> action) {
        action.accept(amount);
    }
}
