package com.pixurvival.contentPackEditor.component.constraint;

import java.util.List;

import com.pixurvival.core.contentPack.validation.annotation.Length;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LengthConstraint {

	private static LengthConstraint NONE = new LengthConstraint(0, Integer.MAX_VALUE);

	private int min;
	private int max;

	public boolean test(String s) {
		return s.length() >= min && s.length() < max;
	}

	public boolean test(List<?> list) {
		return list.size() >= min && list.size() < max;
	}

	public static LengthConstraint fromAnnotation(Length length) {
		return new LengthConstraint(length.min(), length.max());
	}

	public static LengthConstraint none() {
		return NONE;
	}
}
