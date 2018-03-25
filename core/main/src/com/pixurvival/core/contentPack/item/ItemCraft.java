package com.pixurvival.core.contentPack.item;

import javax.xml.bind.annotation.XmlAttribute;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.Getter;

@Getter
public class ItemCraft extends NamedElement {

	@XmlAttribute(name = "duration")
	private double duration;
}
