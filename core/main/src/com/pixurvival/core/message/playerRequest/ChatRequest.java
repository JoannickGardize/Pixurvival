package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRequest implements IPlayerActionRequest {

	private String text;

	@Override
	public void apply(PlayerEntity player) {
		if (text.startsWith("/")) {
			if (text.length() > 1) {
				String returnText = player.getWorld().getCommandManager().process(player, CommandArgsUtils.splitArgs(text.substring(1)));
				if (returnText != null) {
					Log.info(returnText);
				}
			}
		} else {
			// TODO Chat
		}
	}

	@Override
	public boolean isClientPreapply() {
		return false;
	}
}
