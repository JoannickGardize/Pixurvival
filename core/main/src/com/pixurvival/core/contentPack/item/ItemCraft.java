package com.pixurvival.core.contentPack.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Nullable;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.item.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCraft extends IdentifiedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Positive
	private long duration;

	@Valid
	private ItemStack result = new ItemStack();

	@Valid
	private List<ItemStack> recipes = new ArrayList<>();

	@Nullable
	@ElementReference
	private Structure requiredStructure;
}
