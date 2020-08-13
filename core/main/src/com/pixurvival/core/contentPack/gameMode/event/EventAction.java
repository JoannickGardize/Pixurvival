package com.pixurvival.core.contentPack.gameMode.event;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.Action;
import com.pixurvival.core.World;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EventAction implements Action {

	private int eventId;
	private int repeatCount = 0;

	public EventAction(int eventId) {
		this.eventId = eventId;
	}

	@Override
	public void perform(World world) {
		repeatCount++;
		Event event = world.getGameMode().getEvents().get(eventId);
		if (event == null) {
			Log.warn("Unknown event id for current GameMode : " + event);
			return;
		}
		event.perform(world, repeatCount);
		if (event.getRepeatTime() > 0) {
			world.getActionTimerManager().addActionTimer(this, event.getRepeatTime());
		}
	}
}
