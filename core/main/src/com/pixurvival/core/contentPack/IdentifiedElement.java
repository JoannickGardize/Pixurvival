package com.pixurvival.core.contentPack;

import java.io.Serializable;
import java.util.function.Consumer;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Required;
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

	@Required
	@Length(min = 1)
	private String name;

	@Bounds(min = 0)
	private int id;

	public void forEachStatFormulas(Consumer<StatFormula> action) {
		// for override
	}

	public void initialize() {
		// for override
	}

	@Override
	public String toString() {
		return name;
	}
}
