package com.pixurvival.core.contentPack.map;

import java.io.Serializable;

import com.pixurvival.core.contentPack.Dimensions;
import com.pixurvival.core.contentPack.DoubleInterval;
import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
public class Structure extends NamedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private @Setter byte id;

	private StructureType type;

	private boolean solid;

	private SpriteSheet spriteSheet;

	private Dimensions dimensions = new Dimensions(1, 1);

	private double harvestingTime;

	private DoubleInterval respawnTime;

	private ItemReward itemReward;

	private double duration;
}