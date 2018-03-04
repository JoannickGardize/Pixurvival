package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public abstract class NamedElement {

	@Getter
	@XmlAttribute(name = "name", required = true)
	private String name;

}
