package com.pixurvival.core.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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

	private static final NumberFormat TO_STRING_FORMAT = new DecimalFormat("0.0#");
	private double x = 0;
	private double y = 0;

	public Vector2(Vector2 v) {
		x = v.getX();
		y = v.getY();
	}

	public Vector2 copy() {
		return new Vector2(x, y);
	}

	public Vector2 set(Vector2 v) {
		x = v.getX();
		y = v.getY();
		return this;
	}

	public Vector2 set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public static Vector2 fromEuclidean(double length, double direction) {
		return new Vector2(Math.cos(direction) * length, Math.sin(direction) * length);
	}

	public Vector2 setFromEuclidean(double length, double direction) {
		x = Math.cos(direction) * length;
		y = Math.sin(direction) * length;
		return this;
	}

	public Vector2 addX(double x) {
		this.x += x;
		return this;
	}

	public Vector2 addY(double y) {
		this.y += y;
		return this;
	}

	public Vector2 add(Vector2 v) {
		x += v.getX();
		y += v.getY();
		return this;
	}

	public Vector2 add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Vector2 addEuclidean(double length, double direction) {
		x += Math.cos(direction) * length;
		y += Math.sin(direction) * length;
		return this;
	}

	public Vector2 sub(Vector2 v) {
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

	public Vector2 lerp(Vector2 target, double delta) {
		x += (target.x - x) * delta;
		y += (target.y - y) * delta;
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

	public double angleToward(Vector2 other) {
		return Math.atan2(other.y - y, other.x - x);
	}

	public double angleToward(double x, double y) {
		return Math.atan2(y - this.y, x - this.x);
	}

	public double distanceSquared(Vector2 v) {
		double dx = x - v.x;
		double dy = y - v.y;
		return dx * dx + dy * dy;
	}

	public double distance(Vector2 v) {
		return Math.sqrt(distanceSquared(v));
	}

	public double distanceSquared(double x, double y) {
		double dx = this.x - x;
		double dy = this.y - y;
		return dx * dx + dy * dy;
	}

	public boolean epsilonEquals(Vector2 other, double epsilon) {
		return other != null && Math.abs(other.x - x) <= epsilon && Math.abs(other.y - y) <= epsilon;

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(TO_STRING_FORMAT.format(x));
		sb.append(", ");
		sb.append(TO_STRING_FORMAT.format(y));
		sb.append(")");
		return sb.toString();
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
