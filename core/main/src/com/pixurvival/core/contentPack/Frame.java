package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

@Getter
public class Frame {
	@XmlAttribute(required = true)
	private int x;
	@XmlAttribute(required = true)
	private int y;
}
