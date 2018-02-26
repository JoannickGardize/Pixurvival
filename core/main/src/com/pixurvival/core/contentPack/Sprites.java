package com.pixurvival.core.contentPack;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sprites")
public class Sprites extends NamedElementSet<SpriteSheet> {

	@Override
	@XmlElement(name = "spriteSheet")
	public List<SpriteSheet> getListView() {
		return super.getListView();
	}
}
