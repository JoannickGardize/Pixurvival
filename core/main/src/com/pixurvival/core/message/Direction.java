package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.Getter;

@Getter
public enum Direction {

	EAST(0),
	NORTH_EAST(Math.PI / 4.0),
	NORTH(Math.PI / 2.0),
	NORTH_WEST(Math.PI * 3.0 / 4.0),
	WEST(Math.PI),
	SOUTH_WEST(-Math.PI * 3.0 / 4.0),
	SOUTH(-Math.PI / 2.0),
	SOUTH_EAST(-Math.PI / 4.0);

	static {
		for (byte i = 0; i < Direction.values().length; i++) {
			Direction.values()[i].id = i;
		}
	}

	private double angle;
	private byte id;

	private Direction(double angle) {
		this.angle = angle;
	}

	/**
	 * Return the closest cardinal direction of the given angle.
	 * 
	 * @param angle
	 *            The normalized angle, in radians ( ]-Pi;Pi] )
	 * @return The closest cardinal direction (North, East, South, West). For
	 *         ambiguous angles (e.g. Pi/4), The closest cardinal to south is given.
	 */
	public static Direction closestCardinal(double angle) {
		if (angle <= Math.PI / 4 && angle > -Math.PI / 4) {
			return EAST;
		} else if (angle >= 3.0 / 4.0 * Math.PI || angle < -3.0 / 4.0 * Math.PI) {
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
			output.write(object.getId());
		}

		@Override
		public Direction read(Kryo kryo, Input input, Class<Direction> type) {
			return Direction.values()[input.readByte()];
		}

	}
}
