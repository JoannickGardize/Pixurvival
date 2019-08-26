package com.pixurvival.server.util;

import lombok.Getter;

public class AveragePerSecondHelper {

	private long currentStartTime = System.currentTimeMillis();
	private int sum;
	private int count;
	private @Getter int average;
	private @Getter int maximum;

	public boolean add(int value) {
		boolean changed = false;
		if (value > maximum) {
			maximum = value;
			changed = true;
		}
		long time = System.currentTimeMillis();
		if (time - currentStartTime < 1000) {
			sum += value;
			count++;
			return changed;
		} else {
			if (count == 0) {
				count = 1;
			}
			average = sum / count;
			sum = value;
			count = 1;
			currentStartTime = time;
			return true;
		}
	}
}
