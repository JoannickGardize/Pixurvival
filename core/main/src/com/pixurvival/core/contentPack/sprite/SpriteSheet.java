package com.pixurvival.core.contentPack.sprite;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.ResourceReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpriteSheet extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	@Bounds(min = 1)
	private int width;

	@Bounds(min = 1)
	private int height;

	@Required
	@ResourceReference
	private String image;

	@Required
	@ElementReference
	private AnimationTemplate animationTemplate;

	@ElementReference
	private EquipmentOffset equipmentOffset;

	private double heightOffset;

}
