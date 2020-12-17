package com.pixurvival.core.map;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.pixurvival.core.map.chunk.ChunkGroupChangeHelper;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.util.Vector2;

public class ChunkGroupChangeHelperTest {

	@Test
	public void moveTest() {
		ChunkGroupChangeHelper changeHelper = new ChunkGroupChangeHelper();
		Set<ChunkPosition> actualNew = new HashSet<>();
		Set<ChunkPosition> actualOld = new HashSet<>();

		Consumer<ChunkPosition> insertNewAction = position -> {
			if (!actualNew.add(position)) {
				Assertions.fail("The result already contains " + position);
			}
		};

		Consumer<ChunkPosition> insertOldAction = position -> {
			if (!actualOld.add(position)) {
				Assertions.fail("The result already contains " + position);
			}
		};

		changeHelper.move(new Vector2(16, 16), 32, insertNewAction, insertOldAction);

		Set<ChunkPosition> expected = new HashSet<>();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				expected.add(new ChunkPosition(x, y));
			}
		}

		Assertions.assertEquals(expected, actualNew);
		Assertions.assertEquals(new HashSet<>(), actualOld);

		actualNew.clear();
		actualOld.clear();
		changeHelper.move(new Vector2(48, 48), 32, insertNewAction, insertOldAction);

		expected.clear();
		expected.add(new ChunkPosition(0, 2));
		expected.add(new ChunkPosition(1, 2));
		expected.add(new ChunkPosition(2, 2));
		expected.add(new ChunkPosition(2, 1));
		expected.add(new ChunkPosition(2, 0));

		Assertions.assertEquals(expected, actualNew);

		expected.clear();
		expected.add(new ChunkPosition(-1, -1));
		expected.add(new ChunkPosition(0, -1));
		expected.add(new ChunkPosition(1, -1));
		expected.add(new ChunkPosition(-1, 1));
		expected.add(new ChunkPosition(-1, 0));

		Assertions.assertEquals(expected, actualOld);
	}
}
