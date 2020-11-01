package com.pixurvival.core.contentPack;

import org.junit.Assert;
import org.junit.Test;

public class IntOperatorTest {

	@Test
	public void test() {
		Assert.assertTrue(IntOperator.EQUAL_TO.test(2, 2));
		Assert.assertFalse(IntOperator.EQUAL_TO.test(2, 3));
		Assert.assertTrue(IntOperator.LESS_THAN.test(2, 3));
		Assert.assertFalse(IntOperator.LESS_THAN.test(3, 2));
		Assert.assertTrue(IntOperator.GREATER_THAN.test(3, 2));
		Assert.assertFalse(IntOperator.GREATER_THAN.test(2, 3));
	}
}
