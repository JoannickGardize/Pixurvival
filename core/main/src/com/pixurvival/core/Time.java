package com.pixurvival.core;

import com.pixurvival.core.message.TimeRequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Time {

	private long timeMillis = 0;
	private @Getter(AccessLevel.NONE) long localTimeMillis = 0;
	/**
	 * Utilisé par le client pour prendre en compte la différence de temps entre
	 * le client et le serveur, évalué par des envoi régulier de
	 * {@link TimeRequest}.
	 */
	private @Setter long timeOffsetMillis = 0;
	private double deltaTime = 0;
	private double deltaTimeMillis = 0;
	private double decimalAccumulator = 0;

	public void update(double deltaTimeMillis) {
		this.deltaTimeMillis = deltaTimeMillis;
		deltaTime = deltaTimeMillis / 1000.0;
		long integerPart = (long) deltaTimeMillis;
		decimalAccumulator += deltaTimeMillis - integerPart;
		while (decimalAccumulator > 0.5) {
			localTimeMillis++;
			decimalAccumulator--;
		}
		localTimeMillis += integerPart;

		timeMillis = localTimeMillis + timeOffsetMillis;
	}
}
