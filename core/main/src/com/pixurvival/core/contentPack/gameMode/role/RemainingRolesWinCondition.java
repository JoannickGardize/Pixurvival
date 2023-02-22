package com.pixurvival.core.contentPack.gameMode.role;

import com.pixurvival.core.contentPack.IntOperator;
import com.pixurvival.core.contentPack.gameMode.TeamSet;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.IntWrapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RemainingRolesWinCondition implements WinCondition {

    private static final long serialVersionUID = 1L;

    @ElementReference("<<")
    private List<Role> roles = new ArrayList<>();

    private TeamSet teamSet = TeamSet.ALL;

    private IntOperator operator = IntOperator.EQUAL_TO;

    @Positive
    private int value;

    @Override
    public boolean test(PlayerEntity playerEntity) {
        IntWrapper counter = new IntWrapper(0);
        teamSet.forEach(playerEntity, team -> team.getAliveMembers().forEach(p -> {
            if (roles.contains(p.getRole())) {
                counter.increment();
            }
        }));
        return operator.test(counter.getValue(), value);
    }

}
