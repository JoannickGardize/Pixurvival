package com.pixurvival.core.contentPack;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "tiles")
public class Tiles extends NamedElementSet<Tile> {

	@XmlAttribute(name = "image")
	@XmlJavaTypeAdapter(ImageReferenceAdapter.class)
	private ZipContentReference image;

	@Override
	@XmlElement(name = "tile")
	public List<Tile> getListView() {
		return super.getListView();
	}
}