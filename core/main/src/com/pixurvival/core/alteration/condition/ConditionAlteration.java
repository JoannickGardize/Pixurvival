package com.pixurvival.core.alteration.condition;

import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.team.TeamMember;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ConditionAlteration extends Alteration {

    private static final long serialVersionUID = 1L;

    @Valid
    private List<AlterationCondition> conditions = new ArrayList<>();

    @Valid
    private List<Alteration> trueAlterations = new ArrayList<>();

    @Valid
    private List<Alteration> falseAlterations = new ArrayList<>();

    @Override
    public void targetedApply(TeamMember source, TeamMember target) {
        List<Alteration> listToExecute = trueAlterations;
        for (AlterationCondition condition : conditions) {
            if (!condition.test(source)) {
                listToExecute = falseAlterations;
                break;
            }
        }
        for (Alteration alteration : listToExecute) {
            alteration.apply(source, target);
        }
    }

}
