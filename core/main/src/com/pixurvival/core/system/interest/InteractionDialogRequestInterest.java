package com.pixurvival.core.system.interest;

import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.core.livingEntity.PlayerEntity;

public interface InteractionDialogRequestInterest extends Interest {

    void openDialogRequest(PlayerEntity playerEntity, InteractionDialog interactionDialog);

    void closeDialogRequest(PlayerEntity playerEntity);

    void interactDialogRequest(PlayerEntity playerEntity, int index, boolean splitMode);
}
