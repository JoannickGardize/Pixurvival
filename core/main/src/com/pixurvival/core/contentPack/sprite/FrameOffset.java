package com.pixurvival.core.contentPack.sprite;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class FrameOffset extends Frame implements Serializable {

	private static final long serialVersionUID = 1L;

	private int offsetX;
	private int offsetY;
	private boolean back;

	public FrameOffset(int x, int y) {
		super(x, y);

	}
}
