package com.pixurvival.core.contentPack.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.item.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
