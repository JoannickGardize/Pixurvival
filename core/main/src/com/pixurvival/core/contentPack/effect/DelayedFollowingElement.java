package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.contentPack.FloatHolder;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DelayedFollowingElement implements Serializable, FloatHolder {

    private static final long serialVersionUID = 1L;

    @Positive
    private long delay;

    @Valid
    private FollowingElement followingElement;

    @Override
    public float getFloatValue() {
        return delay;
    }
}
