package com.pixurvival.core.contentPack.elementSet;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.util.IndexSet;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public abstract class CollectionElementSet<T extends NamedIdentifiedElement> implements ElementSet<T> {

    private static final long serialVersionUID = 1L;

    @ElementReference
    private @Getter
    @Setter List<T> elements = new ArrayList<>();

    private transient IndexSet indexSet;

    protected boolean collectionContains(int id) {
        if (indexSet == null) {
            indexSet = IndexSet.of(elements.stream().mapToInt(NamedIdentifiedElement::getId).toArray());
        }
        return indexSet.contains(id);
    }
}
