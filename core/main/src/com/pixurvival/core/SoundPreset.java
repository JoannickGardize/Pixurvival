package com.pixurvival.core;

import lombok.Getter;

/**
 * Sound presets have negative IDs values starting from -1, because positive id
 * are for custom ones.
 * 
 * @author SharkHendrix
 *
 */
@Getter
public enum SoundPreset {
	BOW_HIT,
	SWORD_HIT,
	LASER_HIT,
	PUNCH,
	POP,
	BUBBLE,
	SCRUNCH;
}
