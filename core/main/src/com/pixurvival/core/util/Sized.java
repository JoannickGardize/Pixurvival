package com.pixurvival.core.util;

public interface Sized {

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }
}
