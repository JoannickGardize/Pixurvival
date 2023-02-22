package com.pixurvival.core.entity;

import com.pixurvival.core.World;

public interface EntitySupplier {

    Entity get(World world, long id);
}
