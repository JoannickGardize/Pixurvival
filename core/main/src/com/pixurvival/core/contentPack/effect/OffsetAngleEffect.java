package com.pixurvival.core.contentPack.effect;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Effect + relative angle data.
 * 
 * @author SharkHendrix
 *
 */
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class OffsetAngleEffect {

	/**
	 * Angle relative to the source angle (target angle, or angle of the
	 * ancestor effect). In radians.
	 */
	private double offsetAngle;

	/**
	 * Random angle in radians centered to the offset.
	 */
	private double randomAngle;

	private @NonNull Effect effect;
}
