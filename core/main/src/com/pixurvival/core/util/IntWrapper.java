package com.pixurvival.core.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntWrapper {
	private int value;

	public int increment() {
		return ++value;
	}
}
