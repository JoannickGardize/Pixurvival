package fr.sharkhendrix.pixurvival.core;

import java.util.PriorityQueue;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActionTimerManager {

	private @NonNull World world;
	private PriorityQueue<ActionTimer> actionTimerQueue = new PriorityQueue<>();

	public void add(ActionTimer eventTimer) {
		actionTimerQueue.add(eventTimer);
	}

	public void update() {
		boolean actionConsumed = false;
		do {
			ActionTimer actionTimer = actionTimerQueue.peek();
			if (actionTimer != null && world.getTime().getTimeMillis() > actionTimer.getActionTimeMillis()) {
				actionConsumed = true;
				actionTimer.getAction().perform();
				actionTimerQueue.poll();
			}
		} while (actionConsumed);
	}
}
