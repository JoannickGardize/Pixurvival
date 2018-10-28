package com.pixurvival.core.item;

import java.io.Serializable;

import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
public class Item extends NamedElement implements Serializable {

	public static interface Details extends Serializable {

	}

	@Getter
	@Setter
	public static abstract class Equipable implements Details {

		private static final long serialVersionUID = 1L;

		private float strengthBonus;
		private float agilityBonus;
		private float intelligenceBonus;
		private SpriteSheet spriteSheet;

	}

	public static class Edible implements Details {

		private static final long serialVersionUID = 1L;

	}

	public static class Accessory extends Equipable {

		private static final long serialVersionUID = 1L;

	}

	public static class Clothing extends Equipable {

		private static final long serialVersionUID = 1L;

	}

	public static abstract class Weapon extends Equipable {

		private static final long serialVersionUID = 1L;

	}

	public static class MeleeWeapon extends Weapon {

		private static final long serialVersionUID = 1L;

	}

	public static class RangedWeapon extends Equipable {

		private static final long serialVersionUID = 1L;

	}

	@Getter
	@Setter
	public static class Structure implements Details {

		private static final long serialVersionUID = 1L;

		private Structure structure;

	}

	private static final long serialVersionUID = 1L;

	private short id;
	private int maxStackSize;
	private Frame frame;
	private String image;
	private Details details;

	public Item(String name) {
		super(name);
	}
}
