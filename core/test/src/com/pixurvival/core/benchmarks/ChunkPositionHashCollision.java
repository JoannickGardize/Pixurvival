package com.pixurvival.core.benchmarks;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.pixurvival.core.map.chunk.ChunkPosition;

public class ChunkPositionHashCollision {

	/**
	 * @param args
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Map<ChunkPosition, ChunkPosition> map = new HashMap<>();
		map.put(new ChunkPosition(), new ChunkPosition());
		int collisionCount = 0;
		int total = 0;
		for (int x = -1000; x <= 1000; x++) {
			for (int y = -1000; y <= 1000; y++) {
				total++;
				ChunkPosition p = new ChunkPosition(x, y);
				map.put(p, p);
			}
		}
		for (Entry<ChunkPosition, ChunkPosition> e : map.entrySet()) {
			Field f = e.getClass().getDeclaredField("left");
			f.setAccessible(true);
			if (f.get(e) != null) {
				collisionCount++;
			}
		}
		System.out.println("Total: " + total + ", collisions: " + collisionCount);
	}

	/**
	 * Hash transform applied to Java 8's HashMap
	 * 
	 * @param key
	 * @return
	 */
	static final int hash(Object key) {
		int h;
		return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
	}
}
