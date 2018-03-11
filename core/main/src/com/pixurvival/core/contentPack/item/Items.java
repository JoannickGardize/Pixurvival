package com.pixurvival.core.contentPack.item;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pixurvival.core.contentPack.ImageReferenceAdapter;
import com.pixurvival.core.contentPack.NamedElementSet;
import com.pixurvival.core.contentPack.ZipContentReference;
import com.pixurvival.core.item.Item;

import lombok.Getter;

@XmlRootElement(name = "items")
public class Items extends NamedElementSet<Item> {

	@XmlAttribute(name = "image")
	@XmlJavaTypeAdapter(ImageReferenceAdapter.class)
	private @Getter ZipContentReference image;

	@Override
	@XmlElement(name = "item")
	@XmlJavaTypeAdapter(ItemAdapter.class)
	public List<Item> getListView() {
		return super.getListView();
	}

	@Override
	public void finalizeElements() {
		all().values().forEach(i -> i.setImage(image));
	}
}
