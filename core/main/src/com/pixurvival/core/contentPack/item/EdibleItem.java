package com.pixurvival.core.contentPack.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.StatFormula;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdibleItem extends Item {

	private static final long serialVersionUID = 1L;

	@Positive
	private long duration;

	@Valid
	private List<Alteration> alterations = new ArrayList<>();

	@Override
	public void forEachStatFormula(Consumer<StatFormula> action) {
		alterations.forEach(a -> a.forEachStatFormulas(action));
	}

	@Override
	public void forEachAlteration(Consumer<Alteration> action) {
		alterations.forEach(action);
	}
}
