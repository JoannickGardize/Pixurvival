package com.pixurvival.core;

import lombok.*;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * TODO make this a system
 * <p>
 * This class manages actions that have to be executed later.
 * The internal list of action is kept sorted, in this way, the update() method has most of the time a constant O(1) complexity.
 */
@RequiredArgsConstructor
public class ActionTimerManager {

    private @NonNull World world;
    private @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE) Queue<ActionTimer> actionTimerQueue = new PriorityQueue<>();

    /**
     * @param action
     * @param delayMillis
     * @return the computed time at which the action will be executed
     */
    public long addActionTimer(Action action, long delayMillis) {
        long actionTime = world.getTime().getTimeMillis() + delayMillis;
        actionTimerQueue.add(new ActionTimer(action, actionTime));
        return actionTime;
    }

    public void addActionTimerAtWorldTime(Action action, long worldTimeMillis) {
        actionTimerQueue.add(new ActionTimer(action, worldTimeMillis));
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
