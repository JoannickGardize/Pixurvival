package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ElementReference {

	@XmlAttribute(name = "pack")
	private String packRef;

	@XmlValue
	private String name;
}
