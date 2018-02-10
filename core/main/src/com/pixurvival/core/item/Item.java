package com.pixurvival.core.item;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

@Getter
public class Item {

	@XmlAttribute(required = true)
	private String name;
	@XmlAttribute(required = true)
	private int weight;
}
