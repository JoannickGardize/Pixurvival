package com.pixurvival.core.contentPack;

public enum IntOperator {
    EQUAL_TO {
        @Override
        public boolean test(int i1, int i2) {
            return i1 == i2;
        }
    },
    LESS_THAN {
        @Override
        public boolean test(int i1, int i2) {
            return i1 < i2;
        }
    },
    GREATER_THAN {
        @Override
        public boolean test(int i1, int i2) {
            return i1 > i2;
        }
    };

    public abstract boolean test(int i1, int i2);

}
