package com.pixurvival.core.contentPack.sprite;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pixurvival.core.contentPack.NamedElementSet;

@XmlRootElement(name = "sprites")
public class Sprites extends NamedElementSet<SpriteSheet> {

	@Override
	@XmlElement(name = "spriteSheet")
	public List<SpriteSheet> getListView() {
		return super.getListView();
	}
}
