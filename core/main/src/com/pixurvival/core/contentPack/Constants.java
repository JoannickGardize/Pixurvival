package com.pixurvival.core.contentPack;

import java.io.Serializable;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.Getter;

@Getter
public class Constants implements Serializable {

	private static final long serialVersionUID = 1L;

	private SpriteSheet defaultCharacter;

	public void merge(Constants other) {
		if (other.defaultCharacter != null) {
			defaultCharacter = other.defaultCharacter;
		}
	}
}
