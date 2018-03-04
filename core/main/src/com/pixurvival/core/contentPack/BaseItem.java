package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;

@Getter
public class BaseItem extends NamedElement {

	@XmlAttribute(name = "type")
	private ItemType type = ItemType.RESOURCE;

	@XmlAttribute(name = "stackSize")
	private int stackSize = 50;

	@XmlElement(name = "frame", required = true)
	private Frame frame;
}
