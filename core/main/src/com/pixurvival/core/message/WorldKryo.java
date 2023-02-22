package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.pixurvival.core.World;
import lombok.Getter;
import lombok.Setter;

// TODO Kryo#getContext instead of this class
public class WorldKryo extends Kryo {

    private @Setter
    @Getter World world;

    public WorldKryo() {
        setReferences(false);
        setRegistrationRequired(true);
    }
}
