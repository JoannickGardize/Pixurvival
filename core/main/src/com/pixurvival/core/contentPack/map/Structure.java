package com.pixurvival.core.contentPack.map;

import java.io.Serializable;

import com.pixurvival.core.contentPack.Dimensions;
import com.pixurvival.core.contentPack.DoubleInterval;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.item.ItemReward;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Structure extends IdentifiedElement implements Serializable {

	public static interface Details extends Serializable {

	}

	@Getter
	@Setter
	public static class Harvestable implements Details {

		private static final long serialVersionUID = 1L;

		@Bounds(min = 0)
		private double harvestingTime;

		@Required
		@ElementReference
		private ItemReward itemReward;

		@Valid
		@Required
		private DoubleInterval respawnTime = new DoubleInterval();
	}

	@Getter
	@Setter
	public static class ShortLived implements Details {

		private static final long serialVersionUID = 1L;

		@Bounds(min = 0)
		private double duration;
	}

	private static final long serialVersionUID = 1L;

	private boolean solid;

	@Required
	@ElementReference
	private SpriteSheet spriteSheet;

	@Valid
	@Required
	private Dimensions dimensions = new Dimensions(1, 1);

	@Valid
	@Required
	private Details details;

}