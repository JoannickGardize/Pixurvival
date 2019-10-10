package com.pixurvival.core.benchmarks;

public class Test {
	public static void main(String[] args) {

		double deltaTime = 1.0 / 30;
		float damagePerSec = 20;

		float damagePerFrame = damagePerSec * (float) deltaTime;

		System.out.println(damagePerFrame * 30 * 100);
	}
}
