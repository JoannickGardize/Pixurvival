package com.pixurvival.core.contentPack.map;

import java.io.Serializable;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StructureGeneratorEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	@Bounds(min = 0, max = 1, maxInclusive = true)
	private double probability;

	@Required
	@ElementReference
	private Structure structure;
}
