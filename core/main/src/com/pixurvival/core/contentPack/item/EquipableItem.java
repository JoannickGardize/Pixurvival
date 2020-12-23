package com.pixurvival.core.contentPack.item;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.stats.StatModifier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EquipableItem extends Item {

	private static final long serialVersionUID = 1L;

	@Valid
	private List<StatModifier> statModifiers = new ArrayList<>();

}
