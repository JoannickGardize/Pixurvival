package com.pixurvival.core.map;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

public class TiledMapLimits implements TiledMapListener {

	private NavigableMap<Integer, Integer> xCounter = new TreeMap<>();
	private NavigableMap<Integer, Integer> yCounter = new TreeMap<>();

	public int getXMin() {
		Entry<Integer, Integer> entry = xCounter.firstEntry();
		if (entry == null) {
			return 0;
		}
		return entry.getKey();
	}

	public int getXMax() {
		Entry<Integer, Integer> entry = xCounter.lastEntry();
		if (entry == null) {
			return 0;
		}
		return entry.getKey();
	}

	public int getYMin() {
		Entry<Integer, Integer> entry = yCounter.firstEntry();
		if (entry == null) {
			return 0;
		}
		return entry.getKey();
	}

	public int getYMax() {
		Entry<Integer, Integer> entry = yCounter.lastEntry();
		if (entry == null) {
			return 0;
		}
		return entry.getKey();
	}

	@Override
	public void chunkLoaded(Chunk chunk) {
		Position position = chunk.getPosition();
		Integer count = xCounter.get(position.getX());
		if (count == null) {
			xCounter.put(position.getX(), 1);
		} else {
			xCounter.put(position.getX(), ++count);
		}
		count = yCounter.get(position.getY());
		if (count == null) {
			yCounter.put(position.getY(), 1);
		} else {
			yCounter.put(position.getY(), ++count);
		}
	}

	@Override
	public void chunkUnloaded(Chunk chunk) {
		Position position = chunk.getPosition();
		Integer count = xCounter.get(position.getX());
		if (count == 1) {
			xCounter.remove(position.getX());
		} else {
			xCounter.put(position.getX(), --count);
		}
		count = yCounter.get(position.getY());
		if (count == 1) {
			yCounter.remove(position.getY());
		} else {
			yCounter.put(position.getY(), --count);
		}
	}

	@Override
	public void structureChanged(MapStructure mapStructure) {
		// TODO Auto-generated method stub

	}

	@Override
	public void structureAdded(MapStructure mapStructure) {
		// TODO Auto-generated method stub

	}

	@Override
	public void structureRemoved(MapStructure mapStructure) {
		// TODO Auto-generated method stub

	}

}
