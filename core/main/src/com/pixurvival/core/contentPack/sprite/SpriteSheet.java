package com.pixurvival.core.contentPack.sprite;

import com.pixurvival.core.contentPack.ElementReference;
import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.ResourceReference;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SpriteSheet extends NamedElement {

	private static final long serialVersionUID = 1L;

	private int width;

	private int height;

	@ResourceReference
	private String image;

	@ElementReference
	private AnimationTemplate animationTemplate;

	@ElementReference(required = false)
	private EquipmentOffset equipmentOffset;

}
