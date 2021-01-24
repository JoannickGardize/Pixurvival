package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.pixurvival.core.World;

import lombok.Getter;
import lombok.Setter;

public class WorldKryo extends Kryo {

	private @Setter @Getter World world;
}
