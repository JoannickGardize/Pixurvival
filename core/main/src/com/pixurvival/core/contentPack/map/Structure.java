package com.pixurvival.core.contentPack.map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pixurvival.core.contentPack.Dimensions;
import com.pixurvival.core.contentPack.DoubleInterval;
import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.RefAdapter;
import com.pixurvival.core.contentPack.StructureType;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Structure extends NamedElement {

	private @Setter byte id;

	@XmlAttribute(name = "type")
	private StructureType type;

	@XmlAttribute(name = "solid")
	private boolean solid;

	@XmlElement(name = "spriteSheet")
	@XmlJavaTypeAdapter(RefAdapter.SpriteSheetRefAdapter.class)
	private SpriteSheet spriteSheet;

	@XmlElement(name = "dimensions")
	private Dimensions dimensions = new Dimensions(1, 1);

	@XmlElement(name = "harvestingTime")
	private double harvestingTime;

	@XmlElement(name = "respawnTime")
	private DoubleInterval respawnTime;

	@XmlElement(name = "itemReward")
	@XmlJavaTypeAdapter(RefAdapter.ItemRewardRefAdapter.class)
	private ItemReward itemReward;

	@XmlElement(name = "duration")
	private double duration;
}