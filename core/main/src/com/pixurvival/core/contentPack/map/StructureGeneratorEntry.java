package com.pixurvival.core.contentPack.map;

import java.io.Serializable;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;

import lombok.Data;

@Data
public class StructureGeneratorEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	@Bounds(min = 0)
	private double probability;

	@Required
	@ElementReference
	private Structure structure;
}
