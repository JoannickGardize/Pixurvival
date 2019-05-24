package com.pixurvival.core.benchmarks;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.esotericsoftware.kryo.util.IntMap;
import com.pixurvival.core.map.ChunkPosition;

/**
 * <b>HashMap of ChunkPosition vs IntMap of IntMap</b>
 * 
 * <p>
 * The HashMap is about two times slower, because of the key instantiation, in
 * practice the ChunkPosition is sometimes stored, and this strategy is more
 * flexible.
 * <p>
 * <b>Result example : </b> <br>
 * HashMap of ChunkPosition : 105 ms <br>
 * IntMap of IntMap : 42 ms
 * <p>
 * <b>Actual Conclusion</b> : Keeping HashMap despite of the worse performance,
 * because it is more readable and flexible.
 * 
 * @author SharkHendrix
 *
 */
public class ChunkMapBenchmark {

	public static final int SQUARE_SIZE = 100;
	public static final int GET_COUNT = 1_000_000;

	public static void main(String[] args) {
		Random random = new Random();

		BenchmarkUtil.time("HashMap of ChunkPosition", () -> {

			Map<ChunkPosition, Object> map = new HashMap<>();
			for (int x = 0; x < SQUARE_SIZE; x++) {
				for (int y = 0; y < SQUARE_SIZE; y++) {
					map.put(new ChunkPosition(x, y), new Object());
				}
			}

			for (int i = 0; i < GET_COUNT; i++) {
				ChunkPosition position = new ChunkPosition(random.nextInt(SQUARE_SIZE), random.nextInt(SQUARE_SIZE));
				map.get(position);
			}
		});

		BenchmarkUtil.time("IntMap of IntMap", () -> {

			IntMap<IntMap<Object>> map = new IntMap<>();
			for (int x = 0; x < SQUARE_SIZE; x++) {
				for (int y = 0; y < SQUARE_SIZE; y++) {
					IntMap<Object> yMap = map.get(x);
					if (yMap == null) {
						yMap = new IntMap<>();
						map.put(x, yMap);
					}
					yMap.put(y, new Object());
				}
			}

			for (int i = 0; i < GET_COUNT; i++) {
				int x = random.nextInt(SQUARE_SIZE);
				int y = random.nextInt(SQUARE_SIZE);
				IntMap<Object> yMap = map.get(x);
				if (yMap != null) {
					yMap.get(y);
				}
			}
		});
	}

}
