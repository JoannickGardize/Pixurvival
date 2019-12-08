package com.pixurvival.core.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IntWrapper {
	private int value;

	public void add(int toAdd) {
		value += toAdd;
	}

	public void increment() {
		++value;
	}

	public void decrement() {
		--value;
	}
}
