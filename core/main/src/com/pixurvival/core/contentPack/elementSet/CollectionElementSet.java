package com.pixurvival.core.contentPack.elementSet;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.util.IdSetHelper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CollectionElementSet<T extends NamedIdentifiedElement> implements ElementSet<T> {

    private static final long serialVersionUID = 1L;

    @ElementReference
    private @Getter
    @Setter List<T> elements = new ArrayList<>();

    private transient IdSetHelper idSetHelper = new IdSetHelper();

    private transient Set<T> elementSet;

    protected Set<Integer> getIdSet() {
        return idSetHelper.get(elements);
    }

    protected Set<T> getElementSet() {
        if (elementSet == null) {
            elementSet = new HashSet<>(elements);
        }
        return elementSet;
    }

}
