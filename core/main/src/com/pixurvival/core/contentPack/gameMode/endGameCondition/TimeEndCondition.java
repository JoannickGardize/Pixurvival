package com.pixurvival.core.contentPack.gameMode.endGameCondition;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeEndCondition extends EndGameCondition {

    private static final long serialVersionUID = 1L;

    @Positive
    private long time;

    @Override
    public boolean update(World world) {
        return world.getTime().getTimeMillis() >= time;
    }

}
