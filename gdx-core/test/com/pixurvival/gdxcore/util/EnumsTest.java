package com.pixurvival.gdxcore.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumsTest {

	private enum TestEnum {
		A,
		B;
	}

	@Test
	public void valueOfOrNullTest() {
		Assertions.assertEquals(TestEnum.A, Enums.valueOfOrNull(TestEnum.class, "A"));
		Assertions.assertNull(Enums.valueOfOrNull(TestEnum.class, "C"));

	}
}
