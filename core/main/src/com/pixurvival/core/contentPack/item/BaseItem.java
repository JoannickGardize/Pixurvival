package com.pixurvival.core.contentPack.item;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.RefAdapter;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.Getter;

@Getter
public class BaseItem extends NamedElement {

	@XmlAttribute(name = "type")
	private ItemType type = ItemType.RESOURCE;

	@XmlAttribute(name = "stackSize")
	private int stackSize = 50;

	@XmlElement(name = "frame", required = true)
	private Frame frame;

	@XmlElement(name = "strengthBonus", required = true)
	private float strengthBonus;

	@XmlElement(name = "agilityBonus", required = true)
	private float agilityBonus;

	@XmlElement(name = "intelligenceBonus", required = true)
	private float intelligenceBonus;

	@XmlElement(name = "spriteSheet")
	@XmlJavaTypeAdapter(RefAdapter.SpriteSheetRefAdapter.class)
	private SpriteSheet spriteSheet;
}
