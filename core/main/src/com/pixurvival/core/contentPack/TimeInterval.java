package com.pixurvival.core.contentPack;

import com.pixurvival.core.contentPack.validation.annotation.Positive;
import lombok.Data;

import java.io.Serializable;
import java.util.Random;

@Data
public class TimeInterval implements Serializable {

    private static final long serialVersionUID = 1L;

    @Positive
    private long min;

    @Positive
    private long max;

    public long next(Random random) {
        int randomRange = (int) (max - min);
        if (randomRange > 0) {
            return random.nextInt(randomRange) + min;
        } else {
            return min;
        }
    }
}
