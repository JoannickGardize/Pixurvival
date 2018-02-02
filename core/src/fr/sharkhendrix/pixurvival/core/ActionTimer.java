package fr.sharkhendrix.pixurvival.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActionTimer implements Comparable<ActionTimer> {

	private Action action;
	private long actionTimeMillis;

	@Override
	public int compareTo(ActionTimer o) {
		return (int) (actionTimeMillis - o.actionTimeMillis);
	}
}
