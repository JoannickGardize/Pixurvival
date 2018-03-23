package com.pixurvival.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActionTimer implements Comparable<ActionTimer> {

	private Action action;
	private long actionTimeMillis;

	public ActionTimer(Action action, double actionTime) {
		this(action, (long) (actionTime * 1000));
	}

	@Override
	public int compareTo(ActionTimer o) {
		return (int) (actionTimeMillis - o.actionTimeMillis);
	}
}
