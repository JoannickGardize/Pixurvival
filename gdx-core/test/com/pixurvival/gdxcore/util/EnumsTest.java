package com.pixurvival.gdxcore.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class EnumsTest {

	private enum TestEnum {
		A,
		B;
	}

	@Test
	public void valueOfOrNullTest() {
		assertEquals(TestEnum.A, Enums.valueOfOrNull(TestEnum.class, "A"));
		assertNull(Enums.valueOfOrNull(TestEnum.class, "C"));

	}
}
