package com.pixurvival.core.contentPack.map;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pixurvival.core.contentPack.NamedElementSet;

@XmlRootElement(name = "mapGenerators")
public class MapGenerators extends NamedElementSet<MapGenerator> {

	@Override
	@XmlElement(name = "mapGenerator")
	public List<MapGenerator> getListView() {
		return super.getListView();
	}
}
