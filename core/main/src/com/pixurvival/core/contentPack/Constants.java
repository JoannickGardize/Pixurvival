package com.pixurvival.core.contentPack;

import java.io.Serializable;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Constants implements Serializable {

	private static final long serialVersionUID = 1L;

	private SpriteSheet defaultCharacter;
}
