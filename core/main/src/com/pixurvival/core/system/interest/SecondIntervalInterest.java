package com.pixurvival.core.system.interest;

public interface SecondIntervalInterest extends Interest {

    /**
     * @param deltaTime always 1 (second)
     */
    void tick(float deltaTime);
}
