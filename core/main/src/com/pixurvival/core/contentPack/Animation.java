package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;

@Getter
public class Animation {
	@XmlAttribute(name = "action", required = true)
	private ActionAnimation action;

	@XmlElement(name = "frame", required = true)
	private Frame[] frames;
}
