package com.pixurvival.core.contentPack;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pixurvival.core.contentPack.ActionAnimation.ActionAnimationAdapter;
import com.pixurvival.core.message.Direction;
import com.pixurvival.core.util.BeanUtils;

@XmlJavaTypeAdapter(ActionAnimationAdapter.class)
public enum ActionAnimation {
	MOVE_RIGHT,
	MOVE_UP,
	MOVE_LEFT,
	MOVE_DOWN,
	STAND_RIGHT,
	STAND_UP,
	STAND_LEFT,
	STAND_DOWN;

	private static Map<Direction, ActionAnimation> moveByDirection = new HashMap<>();
	private static Map<Direction, ActionAnimation> standByDirection = new HashMap<>();

	static {
		moveByDirection.put(Direction.EAST, MOVE_RIGHT);
		moveByDirection.put(Direction.NORTH, MOVE_UP);
		moveByDirection.put(Direction.SOUTH, MOVE_DOWN);
		moveByDirection.put(Direction.WEST, MOVE_LEFT);

		standByDirection.put(Direction.EAST, STAND_RIGHT);
		standByDirection.put(Direction.NORTH, STAND_UP);
		standByDirection.put(Direction.SOUTH, STAND_DOWN);
		standByDirection.put(Direction.WEST, STAND_LEFT);
	}

	public static ActionAnimation getMoveFromDirection(Direction direction) {
		return moveByDirection.get(direction);
	}

	public static ActionAnimation getStandFromDirection(Direction direction) {
		return standByDirection.get(direction);
	}

	public static ActionAnimation fromValue(String v) {
		return ActionAnimation.valueOf(BeanUtils.camelToUpperCase(v));
	}

	public static class ActionAnimationAdapter extends XmlAdapter<String, ActionAnimation> {

		@Override
		public ActionAnimation unmarshal(String v) throws Exception {
			return ActionAnimation.fromValue(v);
		}

		@Override
		public String marshal(ActionAnimation v) throws Exception {
			return BeanUtils.upperToCamelCase(v.name());
		}

	}
}