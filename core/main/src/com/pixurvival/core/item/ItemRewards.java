package com.pixurvival.core.item;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pixurvival.core.contentPack.NamedElementSet;

@XmlRootElement(name = "itemRewards")
public class ItemRewards extends NamedElementSet<ItemReward> {

	@Override
	@XmlElement(name = "itemReward")
	public List<ItemReward> getListView() {
		return super.getListView();
	}
}
