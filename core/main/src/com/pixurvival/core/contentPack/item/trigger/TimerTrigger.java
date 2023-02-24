package com.pixurvival.core.contentPack.item.trigger;

import com.pixurvival.core.contentPack.validation.annotation.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimerTrigger extends Trigger {

    @Positive
    private long startDelay;

    @Positive
    private long interval;
}
