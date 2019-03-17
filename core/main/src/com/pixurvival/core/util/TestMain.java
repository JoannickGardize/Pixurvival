package com.pixurvival.core.util;

public class TestMain {

	public static void main(String[] args) {
		int x = 1;
		int y = 1;
		System.out.println(Integer.toBinaryString(x << 16 ^ y));
		x = -1;
		y = -1;
		System.out.println(Integer.toBinaryString(x << 16 ^ y));

	}
}
