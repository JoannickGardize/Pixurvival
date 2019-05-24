package com.pixurvival.core.map.analytics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

public class BooleanExtensibleGrid {

	@Getter
	private enum Direction {
		NORTH(0, 1),
		EAST(1, 0),
		SOUTH(0, -1),
		WEST(-1, 0);
		static {
			for (Direction value : values()) {
				value.next = values()[Math.floorMod(value.ordinal() + 1, 4)];
				value.previous = values()[Math.floorMod(value.ordinal() - 1, 4)];
				value.opposite = values()[Math.floorMod(value.ordinal() + 2, 4)];
			}
		}

		private int normalX;
		private int normalY;
		private Direction next;
		private Direction previous;
		private Direction opposite;

		private Direction(int normalX, int normalY) {
			this.normalX = normalX;
			this.normalY = normalY;
		}

	}

	private static Position tmpPosition = new Position();

	private Set<Position> positions = new HashSet<>();

	public int size() {
		return positions.size();
	}

	public boolean get(int x, int y) {
		tmpPosition.set(x, y);
		return positions.contains(tmpPosition);
	}

	public boolean get(Position position) {
		return positions.contains(position);
	}

	public boolean get(Position position, int interval) {
		return get(position.getXGroup(interval), position.getYGroup(interval));
	}

	public void set(int x, int y, boolean value) {
		if (value) {
			positions.add(new Position(x, y));
		} else {
			positions.remove(new Position(x, y));
		}
	}

	public void set(Position position, boolean value) {
		set(position.getX(), position.getY(), value);
	}

	public void set(Position position, int interval, boolean value) {
		set(position.getXGroup(interval), position.getYGroup(interval), value);

	}

	public Position getMostCenteredPoint() {
		if (positions.isEmpty()) {
			return null;
		}
		Position result = new Position();
		for (Position position : positions) {

			result.addX(position.getX());
			result.addY(position.getY());
		}
		result.setX((int) Math.round((double) result.getX() / positions.size()));
		result.setY((int) Math.round((double) result.getY() / positions.size()));
		if (get(result)) {
			return result;
		}
		boolean found = false;
		for (int i = 1; !found; i++) {
			found = result.searchPositionInSquare(i, (x, y) -> {
				if (get(x, y)) {
					result.set(x, y);
					return true;
				} else {
					return false;
				}
			});
		}
		return result;
	}

	public Position[] getExternalPoints() {

		if (positions.isEmpty()) {
			return new Position[0];
		}
		Position startPosition = positions.iterator().next();
		for (Position position : positions) {
			if (position.getX() < startPosition.getX()) {
				startPosition = position;
			}
		}
		if (!hasNeighbor(startPosition)) {
			return new Position[] { startPosition };
		}
		List<Position> externalPositions = new ArrayList<>();
		externalPositions.add(startPosition);
		Position currentPosition = startPosition.copy();
		Direction currentDirection = Direction.NORTH;
		do {
			Direction nextDirection = currentDirection.getPrevious();
			while (!get(currentPosition.getX() + nextDirection.getNormalX(), currentPosition.getY() + nextDirection.getNormalY())) {
				nextDirection = nextDirection.getNext();
			}
			currentPosition.addX(nextDirection.getNormalX());
			currentPosition.addY(nextDirection.getNormalY());
			if (!externalPositions.contains(currentPosition)) {
				externalPositions.add(currentPosition.copy());
			}
			currentDirection = nextDirection;
		} while (!currentPosition.equals(startPosition));
		return externalPositions.toArray(new Position[externalPositions.size()]);
	}

	public void forEachTruePositions(PositionConsumer action) {
		positions.forEach(p -> action.accept(p.getX(), p.getY()));
	}

	private boolean hasNeighbor(Position position) {
		int x = position.getX();
		int y = position.getY();
		return get(x - 1, y) || get(x + 1, y) || get(x, y + 1) || get(x, y - 1);
	}
}
