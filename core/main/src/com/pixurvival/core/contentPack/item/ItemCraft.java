package com.pixurvival.core.contentPack.item;

import java.io.Serializable;

import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.item.ItemStack;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemCraft extends NamedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private @Setter byte id;

	private double duration;

	private ItemStack result;

	private ItemStack[] recipes;
}
