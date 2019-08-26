package com.pixurvival.core.contentPack.effect;

import java.io.Serializable;

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
public class OffsetAngleEffect implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Angle relative to the source angle (target angle, or angle of the
	 * ancestor effect). In radians.
	 */
	private double offsetAngle = 0;

	/**
	 * Random angle in radians centered to the offset.
	 */
	private double randomAngle = 0;

	private @NonNull Effect effect;
}
