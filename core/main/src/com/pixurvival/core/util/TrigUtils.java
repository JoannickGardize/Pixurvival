package com.pixurvival.core.util;

public class TrigUtils {

	private static final int SIN_BITS = 14;
	private static final int SIN_MASK = ~(-1 << SIN_BITS);
	public static final int SIN_COUNT = SIN_MASK + 1;

	private static final double radFull = Math.PI * 2;
	private static final double degFull = 360;
	private static final double radToIndex = SIN_COUNT / radFull;
	private static final double degToIndex = SIN_COUNT / degFull;

	/** multiply by this to convert from radians to degrees */
	public static final double radiansToDegrees = 180 / Math.PI;
	public static final double radDeg = radiansToDegrees;
	/** multiply by this to convert from degrees to radians */
	public static final double degreesToRadians = Math.PI / 180;
	public static final double degRad = degreesToRadians;

	static final double[] table = new double[SIN_COUNT];

	static {
		for (int i = 0; i < SIN_COUNT; i++) {
			table[i] = Math.sin((i + 0.5f) / SIN_COUNT * radFull);
		}
		for (int i = 0; i < 360; i += 90)
			table[(int) (i * degToIndex) & SIN_MASK] = Math.sin(i * degreesToRadians);
	}

	/** Returns the sine in radians from a lookup table. */
	public static double sin(float radians) {
		return table[(int) (radians * radToIndex) & SIN_MASK];
	}

	/** Returns the cosine in radians from a lookup table. */
	public static double cos(double radians) {
		return table[(int) ((radians + Math.PI / 2) * radToIndex) & SIN_MASK];
	}

	/**
	 * Returns atan2 in radians, faster but less accurate than Math.atan2. Average
	 * error of 0.00231 radians (0.1323 degrees), largest error of 0.00488 radians
	 * (0.2796 degrees).
	 */
	public static double atan2(float y, float x) {
		if (x == 0f) {
			if (y > 0f)
				return Math.PI / 2;
			if (y == 0f)
				return 0f;
			return -Math.PI / 2;
		}
		final double atan, z = y / x;
		if (Math.abs(z) < 1f) {
			atan = z / (1f + 0.28f * z * z);
			if (x < 0f)
				return atan + (y < 0f ? -Math.PI : Math.PI);
			return atan;
		}
		atan = Math.PI / 2 - z / (z * z + 0.28);
		return y < 0f ? atan - Math.PI : atan;
	}
}
