package com.pixurvival.core.contentPack.sprite;

import com.pixurvival.core.contentPack.NamedElement;

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

	private String image;

	private AnimationTemplate animationTemplate;

	private EquipmentOffset equipmentOffset;

}
