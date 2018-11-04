package com.pixurvival.core.contentPack.sprite;

import java.util.EnumMap;
import java.util.Map;

import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.validation.ItemCollection;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class AnimationTemplate extends NamedElement {

	private static final long serialVersionUID = 1L;

	@ItemCollection(minQuantity = 1)
	private Map<ActionAnimation, Animation> animations = new EnumMap<>(ActionAnimation.class);

}
