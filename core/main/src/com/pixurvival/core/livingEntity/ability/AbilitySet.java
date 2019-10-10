package com.pixurvival.core.livingEntity.ability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;

import lombok.Getter;
import lombok.Setter;

public class AbilitySet<T extends Ability> extends IdentifiedElement implements Iterable<T> {

	private static final long serialVersionUID = 1L;

	private @Getter @Setter List<T> abilities = new ArrayList<>();

	public void add(T ability) {
		ability.setId((byte) abilities.size());
		abilities.add(ability);
	}

	@SuppressWarnings("unchecked")
	public void addSilence() {
		abilities.add(0, (T) new SilenceAbility());
		for (int i = 0; i < abilities.size(); i++) {
			abilities.get(i).setId((byte) i);
		}
	}

	public T get(int id) {
		return abilities.get(id);
	}

	public int size() {
		return abilities.size();
	}

	@Override
	public Iterator<T> iterator() {
		return abilities.iterator();
	}
}
