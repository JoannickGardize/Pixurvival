package com.pixurvival.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Direction {

	EAST(0),
	NORTH_EAST((float) Math.PI / 4f),
	NORTH((float) Math.PI / 2f),
	NORTH_WEST((float) Math.PI * 3f / 4f),
	WEST((float) Math.PI),
	SOUTH_WEST(-(float) Math.PI * 3f / 4f),
	SOUTH(-(float) Math.PI / 2f),
	SOUTH_EAST(-(float) Math.PI / 4f);

	private float angle;

	/**
	 * Returns the closest cardinal direction of the given angle.
	 * 
	 * @param angle
	 *            The normalized angle, in radians ( ]-Pi;Pi] )
	 * @return The closest cardinal direction (North, East, South, West). For
	 *         ambiguous angles (e.g. Pi/4), The closest cardinal to south is
	 *         given.
	 */
	public static Direction closestCardinalDirection(float angle) {
		if (angle <= (float) Math.PI / 4 && angle > -(float) Math.PI / 4) {
			return EAST;
		} else if (angle >= 3f / 4f * (float) Math.PI || angle < -3f / 4f * (float) Math.PI) {
			return WEST;
		} else if (angle > 0) {
			return NORTH;
		} else {
			return SOUTH;
		}
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<Direction> {

		@Override
		public void write(Kryo kryo, Output output, Direction object) {
			output.write(object.ordinal());
		}

		@Override
		public Direction read(Kryo kryo, Input input, Class<Direction> type) {
			return Direction.values()[input.readByte()];
		}

	}
}
