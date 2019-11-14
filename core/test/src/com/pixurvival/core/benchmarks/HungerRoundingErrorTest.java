package com.pixurvival.core.benchmarks;

import com.pixurvival.core.livingEntity.PlayerEntity;

public class HungerRoundingErrorTest {

	public static void main(String[] args) {
		test(30);
		test(1);
	}

	private static void test(float updatePerSecond) {
		float minusPerTick = PlayerEntity.HUNGER_DECREASE / updatePerSecond;
		float hunger = 100;
		int i = 0;
		while (hunger > 0) {
			hunger -= minusPerTick;
			i++;
		}
		System.out.println("update per second : " + updatePerSecond + ", minutes elapsed : " + i / updatePerSecond / 60f);
	}
}
