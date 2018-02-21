package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlValue;

import lombok.Getter;

@Getter
public class Tile extends NamedElement {
	@XmlAttribute(name = "solid")
	private boolean solid = false;
	@XmlAttribute(name = "frameDuration")
	private float frameDuration;
	@XmlValue
	@XmlElementWrapper(name = "velocityFactor")
	private float velocityFactor = 1;
	@XmlElement(name = "frame")
	private Frame[] frames;
}
