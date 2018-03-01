package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

public abstract class NamedElement {

	@Getter
	@XmlAttribute(name = "name", required = true)
	private String name;

}
