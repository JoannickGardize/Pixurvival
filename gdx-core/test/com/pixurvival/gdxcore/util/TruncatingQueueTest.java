package com.pixurvival.gdxcore.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TruncatingQueueTest {

	@Test
	public void forEachFromHeadTest() {
		TruncatingQueue<Integer> queue = new TruncatingQueue<>(5);
		queue.push(1);
		queue.push(2);
		queue.push(3);
		List<Integer> actual = new ArrayList<>();
		queue.forEachReverse(i -> {
			actual.add(i);
			return i == 2;
		});
		List<Integer> expected = new ArrayList<>();
		expected.add(3);
		expected.add(2);
		Assert.assertEquals(expected, actual);

		queue.push(4);
		queue.push(5);
		queue.push(6);
		actual.clear();
		expected.clear();
		queue.forEachReverse(i -> {
			actual.add(i);
			return false;
		});
		expected.add(6);
		expected.add(5);
		expected.add(4);
		expected.add(3);
		expected.add(2);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void forEachRangeTest() {
		TruncatingQueue<Integer> queue = new TruncatingQueue<>(5);
		queue.push(1);
		queue.push(2);
		queue.push(3);
		queue.push(4);
		List<Integer> actual = new ArrayList<>();
		queue.forEachRange(1, 2, actual::add);
		List<Integer> expected = new ArrayList<>();
		expected.add(2);
		expected.add(3);
		Assert.assertEquals(expected, actual);

	}
}
