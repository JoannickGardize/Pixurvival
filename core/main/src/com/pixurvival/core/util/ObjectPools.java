package com.pixurvival.core.util;

import com.pixurvival.core.message.WorldUpdate;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectPools {

    private static @Getter ObjectPool<WorldUpdate> worldUpdatePool = new ObjectPool<>(WorldUpdate::new);
}
