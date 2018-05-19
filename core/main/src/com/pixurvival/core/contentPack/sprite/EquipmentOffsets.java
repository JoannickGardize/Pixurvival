package com.pixurvival.core.contentPack.sprite;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pixurvival.core.contentPack.NamedElementSet;

@XmlRootElement(name = "equipmentOffsets")
public class EquipmentOffsets extends NamedElementSet<EquipmentOffset> {

	@Override
	@XmlElement(name = "equipmentOffset")
	public List<EquipmentOffset> getListView() {
		return super.getListView();
	}
}
