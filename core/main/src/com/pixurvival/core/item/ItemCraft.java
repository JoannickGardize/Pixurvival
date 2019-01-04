package com.pixurvival.core.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ItemCraft extends NamedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private double duration;

	private ItemStack result = new ItemStack();

	private List<ItemStack> recipes = new ArrayList<>();

}
