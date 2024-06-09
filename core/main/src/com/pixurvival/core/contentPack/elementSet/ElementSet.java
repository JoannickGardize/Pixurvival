package com.pixurvival.core.contentPack.elementSet;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;

import java.io.Serializable;

public interface ElementSet<T extends NamedIdentifiedElement> extends Serializable {

    default boolean contains(T element) {
        return contains(element.getId());
    }

    boolean contains(int id);
}
