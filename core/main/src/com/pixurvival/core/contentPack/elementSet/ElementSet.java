package com.pixurvival.core.contentPack.elementSet;

import java.io.Serializable;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;

public interface ElementSet<T extends NamedIdentifiedElement> extends Serializable {

	boolean contains(T element);

	boolean containsById(int id);

}
