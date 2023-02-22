package com.pixurvival.core.contentPack.ecosystem;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Ecosystem extends NamedIdentifiedElement {

    private static final long serialVersionUID = 1L;

    @Valid
    private List<StructureSpawner> structureSpawners = new ArrayList<>();

    @Valid
    private DarknessSpawner darknessSpawner = new DarknessSpawner();

    private transient @Setter(AccessLevel.NONE) Map<Integer, List<StructureSpawner>> structureSpawnersPerStructure;

    @Override
    public void initialize() {
        structureSpawnersPerStructure = new HashMap<>();
        for (int i = 0; i < structureSpawners.size(); i++) {
            StructureSpawner spawner = structureSpawners.get(i);
            spawner.setId(i);
            spawner.buildCreatureSet();
            structureSpawnersPerStructure.computeIfAbsent(spawner.getStructure().getId(), ArrayList::new).add(spawner);
        }
        structureSpawners.forEach(s -> {
        });
        darknessSpawner.buildCreatureSet();
        darknessSpawner.setId(-1);
    }

    /**
     * Make sure to use this after a call of {@link #initialize()}
     *
     * @param id
     * @return
     */
    public ChunkSpawner getChunkSpawnerById(int id) {
        if (id == -1) {
            return darknessSpawner;
        } else {
            return structureSpawners.get(id);
        }
    }
}
