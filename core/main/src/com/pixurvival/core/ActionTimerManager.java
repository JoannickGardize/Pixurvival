package com.pixurvival.core;

import java.util.PriorityQueue;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActionTimerManager {

	private @NonNull World world;
	private PriorityQueue<ActionTimer> actionTimerQueue = new PriorityQueue<>();

	public void addActionTimer(Action action, long timeMillis) {
		actionTimerQueue.add(new ActionTimer(action, world.getTime().getTimeMillis() + timeMillis));
	}

	public void update() {
		boolean actionConsumed = false;
		do {
			ActionTimer actionTimer = actionTimerQueue.peek();
			if (actionTimer != null && world.getTime().getTimeMillis() >= actionTimer.getActionTimeMillis()) {
				actionConsumed = true;
				actionTimer.getAction().perform();
				actionTimerQueue.poll();
			} else {
				actionConsumed = false;
			}
		} while (actionConsumed);
	}
}
