package com.pixurvival.core.contentPack.item;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pixurvival.core.contentPack.NamedElementSet;

@XmlRootElement(name = "itemCrafts")
public class ItemCrafts extends NamedElementSet<ItemCraft> {

	@Override
	@XmlElement(name = "itemCraft")
	public List<ItemCraft> getListView() {
		return super.getListView();
	}
}
