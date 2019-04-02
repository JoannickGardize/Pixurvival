package com.pixurvival.core.contentPack.sprite;

import java.util.EnumMap;
import java.util.Map;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimationTemplate extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	@Valid
	@Length(min = 1)
	private Map<ActionAnimation, Animation> animations = new EnumMap<>(ActionAnimation.class);

}
