package com.pixurvival.core.id;

import com.pixurvival.core.World;

public interface GenericId<T> {

    T find(World world);
}
