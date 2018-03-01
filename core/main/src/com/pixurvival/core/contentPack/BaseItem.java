package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;

@Getter
public class BaseItem extends NamedElement {

	@XmlAttribute(name = "type")
	private ItemType type;

	@XmlElement(name = "frame")
	private Frame frame;
}
