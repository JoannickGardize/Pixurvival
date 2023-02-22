package com.pixurvival.core.contentPack;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import lombok.Data;

import java.io.Serializable;
import java.util.function.IntPredicate;

@Data
public class IntegerInterval implements IntPredicate, Serializable {

    private static final long serialVersionUID = 1L;

    @Bounds(min = 1)
    private int min = 1;

    @Bounds(min = 1)
    private int max = 1;

    @Override
    public boolean test(int i) {
        return i >= min && i <= max;
    }

}
