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
public class Vector2 implements BaseVector2 {
	public double x = 0;
	public double y = 0;

	public Vector2(BaseVector2 v) {
		x = v.getX();
		y = v.getY();
	}

	public Vector2 copy() {
		return new Vector2(x, y);
	}

	public Vector2 set(BaseVector2 v) {
		x = v.getX();
		y = v.getY();
		return this;
	}

	public Vector2 set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2 setFromEuclidean(double length, double direction) {
		x = Math.cos(direction) * length;
		y = Math.sin(direction) * length;
		return this;
	}

	public Vector2 add(BaseVector2 v) {
		x += v.getX();
		y += v.getY();
		return this;
	}

	public Vector2 sub(BaseVector2 v) {
		x -= v.getX();
		y -= v.getY();
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

	public double angle() {
		return Math.atan2(y, x);
	}

	public double angleTo(Vector2 other) {
		return Math.atan2(other.y - y, other.x - x);
	}

	public double distanceSquared(BaseVector2 v) {
		double dx = x - v.getX();
		double dy = y - v.getY();
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
