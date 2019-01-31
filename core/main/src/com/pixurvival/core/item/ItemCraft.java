package com.pixurvival.core.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ItemCraft extends IdentifiedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Bounds(min = 0)
	private double duration;

	@Valid
	@Required
	private ItemStack result = new ItemStack();

	@Valid
	private List<ItemStack> recipes = new ArrayList<>();

}
