package com.pixurvival.core.contentPack.sprite;

import java.io.Serializable;

import lombok.Data;

@Data
public class FrameOffset implements Serializable {

	private static final long serialVersionUID = 1L;

	private int x;
	private int y;
	private int offsetX;
	private int offsetY;
	private boolean back;
}
