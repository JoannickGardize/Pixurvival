package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pixurvival.core.contentPack.ActionAnimation.ActionAnimationAdapter;
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