package com.pixurvival.core.contentPack.gameMode.event;

import com.pixurvival.core.Action;
import com.pixurvival.core.World;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventAction implements Action {

	private @NonNull World world;
	private @NonNull Event event;
	private int repeatCount = 0;

	@Override
	public void perform() {
		repeatCount++;
		event.perform(world, repeatCount);
		if (event.getRepeatTime() > 0) {
			world.getActionTimerManager().addActionTimer(this, event.getRepeatTime());
		}
	}
}
