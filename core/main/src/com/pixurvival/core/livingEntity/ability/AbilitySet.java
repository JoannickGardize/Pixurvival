package com.pixurvival.core.livingEntity.ability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AbilitySet implements Iterable<Ability> {

	private List<Ability> abilities = new ArrayList<>();

	public void add(Ability ability) {
		ability.setId((byte) abilities.size());
		abilities.add(ability);
	}

	public Ability get(int id) {
		return abilities.get(id);
	}

	public int size() {
		return abilities.size();
	}

	@Override
	public Iterator<Ability> iterator() {
		return abilities.iterator();
	}
}
