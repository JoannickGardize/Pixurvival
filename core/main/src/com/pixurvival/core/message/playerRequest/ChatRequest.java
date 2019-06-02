package com.pixurvival.core.message.playerRequest;

import com.pixurvival.core.chat.ChatEntry;
import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest implements IPlayerActionRequest {

	private String text;

	@Override
	public void apply(PlayerEntity player) {
		if (text.startsWith("/")) {
			if (text.length() > 1) {
				String returnText = player.getWorld().getCommandManager().process(player, CommandArgsUtils.splitArgs(text.substring(1)));
				if (returnText != null) {
					player.getWorld().getChatManager().received(new ChatEntry(player.getWorld(), returnText));
				}
			}
		} else {
			player.getWorld().getChatManager().received(new ChatEntry(player, text));
		}
	}

	@Override
	public boolean isClientPreapply() {
		return false;
	}
}
