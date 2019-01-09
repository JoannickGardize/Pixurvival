package com.pixurvival.core.contentPack.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.sprite.Frame;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tile extends NamedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final byte SPECIAL_TILE = -1;

	private boolean solid = false;
	private double velocityFactor = 1f;
	private List<Frame> frames = new ArrayList<>();
	private String image;
}
