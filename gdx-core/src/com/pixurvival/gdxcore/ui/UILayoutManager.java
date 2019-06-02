package com.pixurvival.gdxcore.ui;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;

public class UILayoutManager {

	public static final int LEFT_SIDE = 0;
	public static final int RIGHT_SIDE = 1;

	private static final int MIN_WIDTH = 150;

	@AllArgsConstructor
	private static class Entry {

		UIWindow window;
		float yPercent;
	}

	private List<Entry> leftEntries = new ArrayList<>();
	private List<Entry> rightEntries = new ArrayList<>();

	public void add(UIWindow window, int side, float yPercent) {
		Entry entry = new Entry(window, yPercent);
		if (side == LEFT_SIDE) {
			leftEntries.add(entry);
		} else {
			rightEntries.add(entry);
		}
	}

	public void resize(int screenWidth, int screenHeight, int gutterWidth) {
		int width = Math.max(gutterWidth, MIN_WIDTH);
		layoutSide(screenHeight, width, leftEntries, 0);
		layoutSide(screenHeight, width, rightEntries, (float) screenWidth - gutterWidth);
	}

	private void layoutSide(int screenHeight, int width, List<Entry> entries, float x) {
		float previousY = 0;
		for (Entry entry : entries) {
			float nextY = entry.yPercent * screenHeight / 100;
			entry.window.setSize(width, nextY - previousY);
			entry.window.setPosition(x, previousY);
			previousY = nextY;
		}
	}

}
