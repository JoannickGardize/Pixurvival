package com.pixurvival.core.livingEntity.stats;

public interface StatListener {

    void statChanged(float oldValue, StatValue statValue);
}
