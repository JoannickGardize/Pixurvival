package com.pixurvival.core.contentPack.structure;

import java.io.Serializable;

import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FactoryFuel implements Serializable {

	private static final long serialVersionUID = 1L;

	@ElementReference
	private Item item;

	@Bounds(min = 0, minInclusive = false)
	private float amount = 1;
}
