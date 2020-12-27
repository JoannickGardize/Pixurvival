package com.pixurvival.core.contentPack.item;

import java.io.Serializable;

import com.pixurvival.core.contentPack.ImageReferenceHolder;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ResourceReference;
import com.pixurvival.core.contentPack.validation.annotation.UnitSpriteFrame;
import com.pixurvival.core.contentPack.validation.annotation.UnitSpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class Item extends NamedIdentifiedElement implements Serializable, ImageReferenceHolder {

	private static final long serialVersionUID = 1L;

	@Bounds(min = 1)
	private int maxStackSize;

	@UnitSpriteFrame
	@Valid
	private Frame frame;

	@UnitSpriteSheet
	@ResourceReference
	private String image;

	public Item(String name, int index) {
		super(name, index);
	}

}
