package com.pixurvival.core.contentPack;

import java.io.Serializable;
import java.util.function.Consumer;

import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.StatFormula;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class IdentifiedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Length(min = 1)
	private String name;

	@Positive
	private int id;

	public void forEachStatFormula(Consumer<StatFormula> action) {
		// for override
	}

	public void forEachAlteration(Consumer<Alteration> action) {

	}

	public void initialize() {
		// for override
	}

	@Override
	public String toString() {
		return name;
	}
}
