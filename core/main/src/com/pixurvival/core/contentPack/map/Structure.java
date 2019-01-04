package com.pixurvival.core.contentPack.map;

import java.io.Serializable;

import com.pixurvival.core.contentPack.Dimensions;
import com.pixurvival.core.contentPack.DoubleInterval;
import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.ItemReward;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Structure extends NamedElement implements Serializable {

	public static interface Details extends Serializable {

	}

	@Getter
	@Setter
	public static class Harvestable implements Details {

		private static final long serialVersionUID = 1L;

		private ItemReward itemReward;
		private double harvestingTime;
		private DoubleInterval respawnTime;
	}

	@Getter
	@Setter
	public static class ShortLived implements Details {

		private static final long serialVersionUID = 1L;

		private double duration;
	}

	private static final long serialVersionUID = 1L;

	private StructureType type;

	private boolean solid;

	private SpriteSheet spriteSheet;

	private Dimensions dimensions = new Dimensions(1, 1);

	private Details details;

}