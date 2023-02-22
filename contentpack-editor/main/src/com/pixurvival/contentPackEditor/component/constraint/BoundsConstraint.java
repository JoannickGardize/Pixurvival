package com.pixurvival.contentPackEditor.component.constraint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Predicate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoundsConstraint implements Predicate<Number> {
    private float min = Float.NEGATIVE_INFINITY;
    private boolean excludeMin = false;
    private float max = Float.POSITIVE_INFINITY;
    private boolean excludeMax = false;

    public BoundsConstraint(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public static BoundsConstraint min(float min) {
        return min(min, false);
    }

    public static BoundsConstraint min(float min, boolean exclude) {
        return new BoundsConstraint(min, exclude, Float.POSITIVE_INFINITY, false);
    }

    public static BoundsConstraint max(float max) {
        return new BoundsConstraint(Float.NEGATIVE_INFINITY, false, max, false);
    }

    public static BoundsConstraint max(float max, boolean exclude) {
        return new BoundsConstraint(Float.NEGATIVE_INFINITY, false, max, exclude);
    }

    public static BoundsConstraint positive() {
        return min(0);
    }

    @Override
    public boolean test(Number value) {
        float floatValue = value.floatValue();
        return (!excludeMin && floatValue >= min || excludeMin && floatValue > min) && (!excludeMax && floatValue <= max || excludeMax && floatValue < max);
    }
}
