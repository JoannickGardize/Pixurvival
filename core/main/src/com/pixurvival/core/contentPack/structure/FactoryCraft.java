package com.pixurvival.core.contentPack.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Unique;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.item.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FactoryCraft implements Serializable {

	private static final long serialVersionUID = 1L;

	@Valid
	@Unique
	private List<ItemStack> recipes = new ArrayList<>();

	@Valid
	@Unique
	private List<ItemStack> results = new ArrayList<>();

	@Positive
	private long duration;

	@Positive
	private float fuelConsumption;
}
