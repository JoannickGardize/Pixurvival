package com.pixurvival.core.id;

import com.pixurvival.core.World;
import com.pixurvival.core.map.StructureEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StructureGenericId<T extends StructureEntity> implements GenericId<T> {

    private final long id;
    private final int x;
    private final int y;

    @Override
    public T find(World world) {
        return null;
    }

}
