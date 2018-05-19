package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlRootElement;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.Data;

@Data
@XmlRootElement(name = "constants")
public class Constants {
	private SpriteSheet defaultCharacter;

	public void merge(Constants other) {
		if (other.defaultCharacter != null) {
			defaultCharacter = other.defaultCharacter;
		}
	}
}
