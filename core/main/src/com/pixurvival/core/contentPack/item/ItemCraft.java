package com.pixurvival.core.contentPack.item;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ItemCraft extends NamedElement {

	private @Setter byte id;

	@XmlAttribute(name = "duration")
	private double duration;

	@XmlElement(name = "result")
	private ItemQuantity result;

	@XmlElementWrapper(name = "recipes")
	@XmlElement(name = "recipe")
	private ItemQuantity[] recipes;
}
