package com.pixurvival.core.contentPack;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntOperatorTest {

	@Test
	public void test() {
		Assertions.assertTrue(IntOperator.EQUAL_TO.test(2, 2));
		Assertions.assertFalse(IntOperator.EQUAL_TO.test(2, 3));
		Assertions.assertTrue(IntOperator.LESS_THAN.test(2, 3));
		Assertions.assertFalse(IntOperator.LESS_THAN.test(3, 2));
		Assertions.assertTrue(IntOperator.GREATER_THAN.test(3, 2));
		Assertions.assertFalse(IntOperator.GREATER_THAN.test(2, 3));
	}
}
