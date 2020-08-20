package com.pixurvival.core.contentPack.elementSet;

import java.io.Serializable;

import com.pixurvival.core.contentPack.IdentifiedElement;

public interface ElementSet<T extends IdentifiedElement> extends Serializable {

	boolean contains(T element);

	boolean containsById(int id);

}
