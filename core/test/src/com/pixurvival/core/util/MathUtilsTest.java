package com.pixurvival.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MathUtilsTest {

	@Test
	public void floorTest() {
		Assertions.assertEquals((int) Math.floor(-1), MathUtils.floor(-1));
		Assertions.assertEquals((int) Math.floor(1), MathUtils.floor(1));
		Assertions.assertEquals((int) Math.floor(5.1), MathUtils.floor(5.1f));
		Assertions.assertEquals((int) Math.floor(13.9), MathUtils.floor(13.9f));
		Assertions.assertEquals((int) Math.floor(-6.5), MathUtils.floor(-6.5f));
		Assertions.assertEquals((int) Math.floor(-6.8), MathUtils.floor(-6.8f));
	}

	@Test
	public void ceilTest() {
		Assertions.assertEquals((int) Math.ceil(-1), MathUtils.ceil(-1));
		Assertions.assertEquals((int) Math.ceil(1), MathUtils.ceil(1));
		Assertions.assertEquals((int) Math.ceil(5.1), MathUtils.ceil(5.1f));
		Assertions.assertEquals((int) Math.ceil(13.9), MathUtils.ceil(13.9f));
		Assertions.assertEquals((int) Math.ceil(-6.5), MathUtils.ceil(-6.5f));
		Assertions.assertEquals((int) Math.ceil(-6.8), MathUtils.ceil(-6.8f));
	}
}
