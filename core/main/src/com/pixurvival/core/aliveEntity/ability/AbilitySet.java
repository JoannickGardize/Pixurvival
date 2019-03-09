package com.pixurvival.core.aliveEntity.ability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pixurvival.core.aliveEntity.AliveEntity;

public class AbilitySet<T extends AliveEntity<T>> implements Iterable<Ability<T>> {

	private List<Ability<T>> abilities = new ArrayList<Ability<T>>();

	public void add(Ability<T> ability) {
		ability.setId(abilities.size());
		abilities.add(ability);
	}

	public Ability<T> get(int id) {
		return abilities.get(id);
	}

	public int size() {
		return abilities.size();
	}

	@Override
	public Iterator<Ability<T>> iterator() {
		return abilities.iterator();
	}
}
