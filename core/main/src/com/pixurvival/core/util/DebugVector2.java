package com.pixurvival.core.util;

public class DebugVector2 extends Vector2 {
	@Override
	public Vector2 set(Vector2 v) {
		System.out.println("set " + v);
		return super.set(v);
	}

	@Override
	public Vector2 set(double x, double y) {
		System.out.println("set " + x + ", " + y);
		return super.set(x, y);
	}

	@Override
	public Vector2 setFromEuclidean(double length, double direction) {
		System.out.println("setFromEuclidean " + length + ", " + direction);
		return super.setFromEuclidean(length, direction);
	}

	@Override
	public Vector2 addX(double x) {
		System.out.println("addX " + x);
		return super.addX(x);
	}

	@Override
	public Vector2 addY(double y) {
		System.out.println("addY " + y);
		return super.addY(y);
	}

	@Override
	public Vector2 add(Vector2 v) {
		System.out.println("add " + v);
		return super.add(v);
	}

	@Override
	public Vector2 addEuclidean(double length, double direction) {
		System.out.println("addEuclidean " + length + ", " + direction);
		return super.addEuclidean(length, direction);
	}

	@Override
	public Vector2 sub(Vector2 v) {
		System.out.println("sub " + v);
		return super.sub(v);
	}

	@Override
	public Vector2 mul(double d) {
		System.out.println("mul " + d);
		return super.mul(d);
	}

	@Override
	public Vector2 div(double d) {
		System.out.println("div " + d);
		return super.div(d);
	}

	@Override
	public Vector2 lerp(Vector2 target, double delta) {
		System.out.println("lerp " + target + ", " + delta);
		return super.lerp(target, delta);
	}
}
