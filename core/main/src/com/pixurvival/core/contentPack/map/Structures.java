package com.pixurvival.core.contentPack.map;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pixurvival.core.contentPack.NamedElementSet;

@XmlRootElement(name = "structures")
public class Structures extends NamedElementSet<Structure> {
	@Override
	@XmlElement(name = "structure")
	public List<Structure> getListView() {
		return super.getListView();
	}
}
