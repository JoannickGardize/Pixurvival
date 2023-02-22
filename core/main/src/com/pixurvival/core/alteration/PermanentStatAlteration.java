package com.pixurvival.core.alteration;

import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.team.TeamMember;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

/**
 * Permanent stats are applied to the base value of the living entity and never
 * disappear, once he died (according to the game mode rule).
 *
 * @author SharkHendrix
 */
@Getter
@Setter
public class PermanentStatAlteration extends UniqueAlteration {

    private static final long serialVersionUID = 1L;

    private StatType statType = StatType.STRENGTH;

    @Valid
    private StatFormula amount = new StatFormula();

    @Override
    public void uniqueApply(TeamMember source, TeamMember entity) {
        entity.getStats().get(statType).addToBase(amount.getValue(source));
    }

    @Override
    public void forEachStatFormulas(Consumer<StatFormula> action) {
        action.accept(amount);
    }
}
