package com.pixurvival.core.util;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
		Assertions.assertEquals(10, count);
		Assertions.assertEquals(45, sum);
	}
}
