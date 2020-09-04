package com.pixurvival.core.util;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class IntUrnTest {

	@Test
	public void drawTest() {
		Random random = new Random();
		IntUrn urn = new IntUrn(10);
		test(random, urn);
	}

	@Test
	public void resetTest() {
		Random random = new Random();
		IntUrn urn = new IntUrn(10);
		test(random, urn);
		urn.reset();
		test(random, urn);
	}

	private void test(Random random, IntUrn urn) {
		int count = 0;
		int sum = 0;
		while (!urn.isEmpty()) {
			sum += urn.draw(random);
			count++;
		}
		Assert.assertEquals(10, count);
		Assert.assertEquals(45, sum);
	}
}
