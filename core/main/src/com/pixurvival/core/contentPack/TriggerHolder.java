package com.pixurvival.core.contentPack;

import com.pixurvival.core.contentPack.trigger.Trigger;

import java.util.List;
import java.util.Map;

public interface TriggerHolder {

    List<Trigger> getTriggers();

    Map<Class<? extends Trigger>, List<Trigger>> getTriggersByType();

    void setTriggersByType(Map<Class<? extends Trigger>, List<Trigger>> triggersByType);
}
