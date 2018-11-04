package com.pixurvival.core.contentPack;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class NamedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private int id;

	@Override
	public final String toString() {
		return name;
	}
}
