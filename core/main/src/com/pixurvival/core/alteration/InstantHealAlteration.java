package com.pixurvival.core.alteration;

import com.pixurvival.core.Healable;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.team.TeamMember;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
public class InstantHealAlteration extends UniqueAlteration {

    private static final long serialVersionUID = 1L;

    @Valid
    private StatFormula amount = new StatFormula();

    @Override
    public void uniqueApply(TeamMember source, TeamMember target) {
        if (target instanceof Healable) {
            ((Healable) target).takeHeal(amount.getValue(source));
        }
    }

    @Override
    public void forEachStatFormulas(Consumer<StatFormula> action) {
        action.accept(amount);
    }
}
