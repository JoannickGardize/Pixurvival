package com.pixurvival.core.contentPack.elementSet;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;

import java.io.Serializable;

public interface ElementSet<T extends NamedIdentifiedElement> extends Serializable {

    boolean contains(T element);

    boolean containsById(int id);
}
