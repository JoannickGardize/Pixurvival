package com.pixurvival.gdxcore.input.processor;

import com.pixurvival.core.World.Type;
import com.pixurvival.gdxcore.PixurvivalGame;

public class RefreshProcessor implements InputActionProcessor {

	@Override
	public void buttonDown() {
		if (PixurvivalGame.getClient().getWorld().getType() == Type.CLIENT) {
			PixurvivalGame.getClient().requestRefresh();
		}
	}
}
