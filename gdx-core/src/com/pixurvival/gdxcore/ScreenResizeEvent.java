package com.pixurvival.gdxcore;

import com.badlogic.gdx.scenes.scene2d.Event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScreenResizeEvent extends Event {

	private int prevScreenWidth;
	private int prevScreenHeight;
	private int newScreenWidth;
	private int newScreenHeight;

	public boolean isValid() {
		return prevScreenWidth != 0 && prevScreenHeight != 0 && newScreenWidth != 0 && newScreenHeight != 0;
	}
}
