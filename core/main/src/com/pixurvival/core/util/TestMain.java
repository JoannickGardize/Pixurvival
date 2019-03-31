package com.pixurvival.core.util;

import java.util.Random;

public class TestMain {

	public static void main(String[] args) {
		// int x = 1;
		// int y = 1;
		// System.out.println(Integer.toBinaryString(x << 16 ^ y));
		// x = -1;
		// y = -1;
		// System.out.println(Integer.toBinaryString(x << 16 ^ y));

		Random random = new Random();
		for (int i = 0; i < 100_000_000; i++) {
			if (random.nextGaussian() > 5) {
				System.out.println("coucuo !");
			}
		}
	}
}
