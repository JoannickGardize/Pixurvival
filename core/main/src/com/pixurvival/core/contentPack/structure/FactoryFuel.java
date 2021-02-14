package com.pixurvival.core.contentPack.structure;

import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FactoryFuel {

	@ElementReference
	private Item item;

	@Positive
	private float amount = 1;
}
