package com.pixurvival.core;

import java.util.PriorityQueue;
import java.util.Queue;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ActionTimerManager {

	private @NonNull World world;
	private @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE) Queue<ActionTimer> actionTimerQueue = new PriorityQueue<>();

	public void addActionTimer(Action action, long timeMillis) {
		actionTimerQueue.add(new ActionTimer(action, world.getTime().getTimeMillis() + timeMillis));
	}

	public void update() {
		boolean actionConsumed = true;
		int initialSize = actionTimerQueue.size();
		int i = 0;
		while (actionConsumed && i < initialSize) {
			ActionTimer actionTimer = actionTimerQueue.peek();
			if (actionTimer != null && world.getTime().getTimeMillis() >= actionTimer.getActionTimeMillis()) {
				actionConsumed = true;
				actionTimer.getAction().perform(world);
				actionTimerQueue.poll();
				i++;
			} else {
				actionConsumed = false;
			}
		}
	}
}
