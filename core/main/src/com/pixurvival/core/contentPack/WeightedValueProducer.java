package com.pixurvival.core.contentPack;

import com.pixurvival.core.contentPack.validation.annotation.ElementReferenceOrValid;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.util.Sized;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

public class WeightedValueProducer<E extends Serializable> implements Serializable, Sized {

    private static final long serialVersionUID = 1L;

    @Data
    public static class Entry<E extends Serializable> implements Serializable {

        private static final long serialVersionUID = 1L;

        @Positive
        private float probability;

        @ElementReferenceOrValid
        private E element;
    }

    @Valid
    private @Getter
    @Setter List<Entry<E>> backingArray = new ArrayList<>();

    private transient float probabilityWeight;

    private transient NavigableMap<Float, E> chooseMap;

    public E next(Random random) {
        ensureChooseMapBuilt();
        return chooseMap.ceilingEntry(random.nextFloat() * probabilityWeight).getValue();
    }

    private void ensureChooseMapBuilt() {
        if (chooseMap != null) {
            return;
        }
        chooseMap = new TreeMap<>();
        probabilityWeight = 0;
        for (Entry<E> entry : backingArray) {
            probabilityWeight += entry.getProbability();
            chooseMap.put(probabilityWeight, entry.getElement());
        }
    }

    @Override
    public int size() {
        return backingArray.size();
    }
}
