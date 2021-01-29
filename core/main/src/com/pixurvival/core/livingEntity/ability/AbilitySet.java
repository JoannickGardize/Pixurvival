package com.pixurvival.core.livingEntity.ability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.alteration.StatFormula;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.Setter;

public class AbilitySet extends NamedIdentifiedElement implements Iterable<Ability> {

	private static final long serialVersionUID = 1L;

	@Valid
	private @Getter @Setter List<Ability> abilities = new ArrayList<>();

	public int add(Ability ability) {
		ability.setId((byte) abilities.size());
		abilities.add(ability);
		return ability.getId();
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
