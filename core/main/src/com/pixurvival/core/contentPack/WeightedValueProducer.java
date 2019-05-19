package com.pixurvival.core.contentPack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class WeightedValueProducer<E extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Data
	public static class Entry<E extends Serializable> implements Serializable {

		private static final long serialVersionUID = 1L;

		private double probability;

		private E element;
	}

	private @Getter @Setter List<Entry<E>> backingArray = new ArrayList<>();

	private transient double probabilityWeight;

	private transient NavigableMap<Double, E> chooseMap;

	public E next(Random random) {
		ensureChooseMapBuilt();
		return chooseMap.ceilingEntry(random.nextDouble() * probabilityWeight).getValue();
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
}
