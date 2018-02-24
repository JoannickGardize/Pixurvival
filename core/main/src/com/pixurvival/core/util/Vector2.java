package com.pixurvival.core.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vector2 {
	public double x = 0;
	public double y = 0;

	public Vector2(Vector2 v) {
		x = v.x;
		y = v.y;
	}

	public void set(Vector2 v) {
		x = v.x;
		y = v.y;
	}

	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2 add(Vector2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public Vector2 sub(Vector2 v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	public Vector2 mul(double d) {
		x *= d;
		y *= d;
		return this;
	}

	public Vector2 div(double d) {
		x /= d;
		y /= d;
		return this;
	}

	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	public double lengthSquared() {
		return x * x + y * y;
	}

	public double distanceSquared(Vector2 v) {
		double dx = x - v.x;
		double dy = y - v.y;
		return dx * dx + dy * dy;
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<Vector2> {

		@Override
		public void write(Kryo kryo, Output output, Vector2 object) {
			output.writeDouble(object.x);
			output.writeDouble(object.y);
		}

		@Override
		public Vector2 read(Kryo kryo, Input input, Class<Vector2> type) {
			return new Vector2(input.readDouble(), input.readDouble());
		}

	}
}
