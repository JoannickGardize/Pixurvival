package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Getter;

@Getter
public class Layer {

	@XmlAttribute(name = "level")
	private float level;
	@XmlElement(name = "tile")
	@XmlJavaTypeAdapter(RefAdapter.TileRefAdapter.class)
	private Tile tile;
}
