package com.pixurvival.core.contentPack.structure;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.item.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FactoryCraft {

	@Valid
	private List<ItemStack> recipes = new ArrayList<>();

	@Valid
	private List<ItemStack> results = new ArrayList<>();

	@Positive
	private long duration;

	@Positive
	private float fuelConsumption;
}
