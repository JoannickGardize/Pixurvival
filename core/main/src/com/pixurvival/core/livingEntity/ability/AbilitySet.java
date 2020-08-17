package com.pixurvival.core.livingEntity.ability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.StatFormula;

import lombok.Getter;
import lombok.Setter;

public class AbilitySet extends IdentifiedElement implements Iterable<Ability> {

	private static final long serialVersionUID = 1L;

	private @Getter @Setter List<Ability> abilities = new ArrayList<>();

	public void add(Ability ability) {
		ability.setId((byte) abilities.size());
		abilities.add(ability);
	}

	public void addSilence() {
		if (!abilities.isEmpty() && abilities.get(0) instanceof SilenceAbility) {
			return;
		}
		abilities.add(0, new SilenceAbility());
		for (int i = 0; i < abilities.size(); i++) {
			abilities.get(i).setId((byte) i);
		}
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

	@Override
	public void forEachStatFormula(Consumer<StatFormula> action) {
		abilities.forEach(a -> a.forEachStatFormulas(action));
	}

	@Override
	public void forEachAlteration(Consumer<Alteration> action) {
		abilities.forEach(a -> a.forEachAlteration(action));
	}
}
