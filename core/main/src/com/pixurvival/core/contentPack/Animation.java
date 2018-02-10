package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;

@Getter
public class Animation {
	@XmlAttribute(required = true)
	private String name;

	@XmlElement(name = "frame", required = true)
	private Frame[] frames;
}
