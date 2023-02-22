package com.pixurvival.core.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FloatComparison {

    GREATER_THAN() {
        @Override
        public boolean test(float d1, float d2) {
            return d1 > d2;
        }

        @Override
        public boolean testPresence(Object o) {
            return o == null;
        }
    },
    LESS_THAN() {
        @Override
        public boolean test(float d1, float d2) {
            return d1 < d2;
        }

        @Override
        public boolean testPresence(Object o) {
            return o != null;
        }
    };

    public abstract boolean test(float d1, float d2);

    /**
     * Test used after an entity / structure lookup method according to a given
     * distance
     *
     * @param o
     * @return
     */
    public abstract boolean testPresence(Object o);

}
