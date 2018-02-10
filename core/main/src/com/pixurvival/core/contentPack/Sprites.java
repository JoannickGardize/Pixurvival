package com.pixurvival.core.contentPack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pixurvival.core.util.ListViewOfMap;

@XmlRootElement(name = "sprites")
public class Sprites {

	private Map<String, SpriteSheet> spriteSheets = new HashMap<>();

	public Map<String, SpriteSheet> getSpriteSheets() {
		return Collections.unmodifiableMap(spriteSheets);
	}

	@XmlElement(name = "spriteSheet")
	public List<SpriteSheet> getSpriteSheetsListView() {
		return new ListViewOfMap<String, SpriteSheet>(spriteSheets, SpriteSheet::getName);
	}
}
