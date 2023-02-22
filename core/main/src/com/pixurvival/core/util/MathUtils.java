package com.pixurvival.core.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtils {

    public static final float EPSILON = 0.0001f;

    public static boolean equals(float d1, float d2) {
        return Math.abs(d1 - d2) < EPSILON;
    }

    public static float normalizeAngle(float angle) {
        float result = angle;
        while (result <= -Math.PI) {
            result += Math.PI * 2;
        }
        while (result > Math.PI) {
            result -= Math.PI * 2;
        }
        return result;
    }

    public static float clamp(float value, float min, float max) {
        if (value > max) {
            return max;
        } else if (value < min) {
            return min;
        } else {
            return value;
        }
    }

    public static int clamp(int value, int min, int max) {
        if (value > max) {
            return max;
        } else if (value < min) {
            return min;
        } else {
            return value;
        }
    }

    public static float linearInterpolate(float start, float end, float alpha) {
        return start + (end - start) * alpha;
    }

    public static float oppositeDirection(float angle) {
        return angle + (float) Math.PI;
    }

    /**
     * This method is <b>a lot</b> faster than {@link Math#floor(float)}
     *
     * @param x
     * @return
     */
    public static int floor(float x) {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }

    /**
     * This method is <b>a lot</b> faster than {@link Math#ceil(float)}
     *
     * @param x
     * @return
     */
    public static int ceil(float x) {
        int xi = (int) x;
        return x > xi ? xi + 1 : xi;
    }
}
