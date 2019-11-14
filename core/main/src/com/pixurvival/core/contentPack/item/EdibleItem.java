package com.pixurvival.core.contentPack.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.StatFormula;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdibleItem extends Item {

	private static final long serialVersionUID = 1L;

	private long duration;
	private List<Alteration> alterations = new ArrayList<>();

	@Override
	public void forEachStatFormulas(Consumer<StatFormula> action) {
		alterations.forEach(a -> a.forEachStatFormulas(action));
	}
}
