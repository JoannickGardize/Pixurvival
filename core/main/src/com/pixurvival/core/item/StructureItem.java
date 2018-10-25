package com.pixurvival.core.item;

import com.pixurvival.core.contentPack.map.Structure;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StructureItem extends Item {

	private static final long serialVersionUID = 1L;

	private Structure structure;

	public StructureItem(String name) {
		super(name);
	}

}
