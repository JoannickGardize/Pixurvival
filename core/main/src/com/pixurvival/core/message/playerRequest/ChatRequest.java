package com.pixurvival.core.message.playerRequest;

import com.pixurvival.core.chat.ChatEntry;
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
        player.getWorld().received(new ChatEntry(player, text));
    }

    @Override
    public boolean isClientPreapply() {
        return false;
    }
}
