package com.pixurvival.core.item;

import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StructureItem extends Item {

	private static final long serialVersionUID = 1L;

	@Required
	@ElementReference
	private Structure structure;

}
