package com.pixurvival.core.util;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class IdSetHelper {

    private Set<Integer> idSet;

    public Set<Integer> get(Collection<? extends NamedIdentifiedElement> collection) {
        if (idSet == null) {
            idSet = new HashSet<>(collection.size(), 1);
            for (NamedIdentifiedElement element : collection) {
                idSet.add(element.getId());
            }
        }
        return idSet;
    }
}
