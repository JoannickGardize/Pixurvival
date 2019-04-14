package com.pixurvival.core.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.ResourceReference;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.ability.Ability;
import com.pixurvival.core.livingEntity.ability.EffectAbility;
import com.pixurvival.core.livingEntity.stats.StatModifier;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Item extends IdentifiedElement implements Serializable {

	public static interface Details extends Serializable {

	}

	public static class Resource implements Details {

		private static final long serialVersionUID = 1L;

	}

	@Getter
	@Setter
	public abstract static class Equipable implements Details {

		private static final long serialVersionUID = 1L;

		@ElementReference
		private SpriteSheet spriteSheet;

		private List<StatModifier> statModifiers = new ArrayList<>();

	}

	public static class Edible implements Details {

		private static final long serialVersionUID = 1L;

	}

	@Getter
	@Setter
	public static class Accessory extends Equipable {

		private static final long serialVersionUID = 1L;

		private Ability specialAbility;

	}

	public static class Clothing extends Equipable {

		private static final long serialVersionUID = 1L;
	}

	@Getter
	@Setter
	public static class Weapon extends Equipable {

		private static final long serialVersionUID = 1L;

		private EffectAbility baseAbility;
		private EffectAbility specialAbility;
	}

	@Getter
	@Setter
	public static class StructureDetails implements Details {

		private static final long serialVersionUID = 1L;

		@Required
		@ElementReference
		private Structure structure;

	}

	private static final long serialVersionUID = 1L;

	@Bounds(min = 1)
	private int maxStackSize;

	@Valid
	private Frame frame;

	@Required
	@ResourceReference
	private String image;

	@Valid
	private Details details;

	public Item(String name, int index) {
		super(name, index);
	}

}
