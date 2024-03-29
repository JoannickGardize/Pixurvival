package com.pixurvival.core.alteration;

import com.pixurvival.core.team.TeamMember;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.function.Consumer;

@Getter
@Setter
public abstract class Alteration implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient int id;

    private AlterationTarget targetType = AlterationTarget.TARGET;

    /**
     * Apply an alteration to a target.
     *
     * @param source The source of this alteration.
     * @param entity The entity targeted by the alteration
     */
    public void apply(TeamMember source, TeamMember target) {
        targetedApply(source, targetType.getFunction().apply(source, target));
    }

    public abstract void targetedApply(TeamMember source, TeamMember target);

    public void forEachStatFormulas(Consumer<StatFormula> action) {
        // for override
    }

}