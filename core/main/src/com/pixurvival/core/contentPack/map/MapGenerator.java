package com.pixurvival.core.contentPack.map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.Getter;

@Getter
public class MapGenerator extends NamedElement {

	@XmlElementWrapper(name = "heightmaps")
	@XmlElement(name = "heightmap")
	private Heightmap[] heightmaps;
}
