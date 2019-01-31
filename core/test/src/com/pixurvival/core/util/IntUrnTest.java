package com.pixurvival.core.util;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class IntUrnTest {

	@Test
	public void test() {
		Random random = new Random();
		IntUrn urn = new IntUrn(10);
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
