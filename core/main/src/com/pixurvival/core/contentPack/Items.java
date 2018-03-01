package com.pixurvival.core.contentPack;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pixurvival.core.item.Item;

@XmlRootElement(name = "items")
public class Items extends NamedElementSet<Item> {
	@Override
	@XmlElement(name = "item")
	@XmlJavaTypeAdapter(ItemAdapter.class)
	public List<Item> getListView() {
		return super.getListView();
	}
}
