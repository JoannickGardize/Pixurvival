package com.pixurvival.core.contentPack.sprite;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnimationTemplate extends NamedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private double frameDuration = 1;

	private Map<ActionAnimation, Animation> animations = new EnumMap<>(ActionAnimation.class);

	public Map<ActionAnimation, Animation> getAnimations() {
		return Collections.unmodifiableMap(animations);
	}
}
