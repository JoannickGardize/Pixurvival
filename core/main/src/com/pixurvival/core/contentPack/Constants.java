package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.Getter;

@Getter
@XmlRootElement(name = "constants")
public class Constants {

	@XmlElement(name = "defaultCharacter")
	@XmlJavaTypeAdapter(RefAdapter.SpriteSheetRefAdapter.class)
	private SpriteSheet defaultCharacter;

	public void merge(Constants other) {
		if (other.defaultCharacter != null) {
			defaultCharacter = other.defaultCharacter;
		}
	}
}
