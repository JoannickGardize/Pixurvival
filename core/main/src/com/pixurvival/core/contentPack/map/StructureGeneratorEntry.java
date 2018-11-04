package com.pixurvival.core.contentPack.map;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StructureGeneratorEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private double probability;
	private Structure structure;
}
