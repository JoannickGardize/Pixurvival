package com.pixurvival.core.message.playerRequest;

import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.system.interest.InteractionDialogRequestInterest;

public class CloseInteractionDialogRequest implements IPlayerActionRequest {

    @Override
    public void apply(PlayerEntity player) {
        player.getWorld().getInterestSubscriptionSet().get(InteractionDialogRequestInterest.class).forEach(i -> i.closeDialogRequest(player));
    }

    @Override
    public boolean isClientPreapply() {
        return false;
    }

}
