package com.pixurvival.core.contentPack.ecosystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.contentPack.IdentifiedElement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ecosystem extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	private List<StructureSpawner> structureSpawners = new ArrayList<>();

	private ChunkSpawner darknessSpawner = new ChunkSpawner();

	private transient @Setter(AccessLevel.NONE) Map<Integer, List<StructureSpawner>> structureSpawnersPerStructure;

	@Override
	public void initialize() {
		structureSpawnersPerStructure = new HashMap<>();
		structureSpawners.forEach(s -> {
			s.buildCreatureSet();
			structureSpawnersPerStructure.computeIfAbsent(s.getStructure().getId(), ArrayList::new).add(s);
		});
	}
}
