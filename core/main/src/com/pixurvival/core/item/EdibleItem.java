package com.pixurvival.core.item;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.livingEntity.alteration.Alteration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdibleItem extends Item {

	private static final long serialVersionUID = 1L;

	private double duration;
	private List<Alteration> alterations = new ArrayList<>();

}
