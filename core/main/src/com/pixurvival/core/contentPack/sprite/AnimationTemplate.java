package com.pixurvival.core.contentPack.sprite;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class AnimationTemplate extends NamedElement {

	private static final long serialVersionUID = 1L;

	private Map<ActionAnimation, Animation> animations = new EnumMap<>(ActionAnimation.class);

	public Map<ActionAnimation, Animation> getAnimations() {
		return Collections.unmodifiableMap(animations);
	}
}
