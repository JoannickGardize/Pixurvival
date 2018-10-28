package com.pixurvival.core.contentPack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public abstract class NamedElement {

	@Getter
	@Setter
	private String name;

	@Override
	public final String toString() {
		return name;
	}
}
