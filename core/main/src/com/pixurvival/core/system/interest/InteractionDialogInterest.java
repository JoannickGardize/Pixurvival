package com.pixurvival.core.system.interest;

import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.core.livingEntity.PlayerEntity;

public interface InteractionDialogInterest extends Interest {

	void dialogOpened(PlayerEntity playerEntity, InteractionDialog interactionDialog);

	void dialogClosed(PlayerEntity playerEntity, InteractionDialog interactionDialog);

	void viewingDialogChanged(InteractionDialog interactionDialog);
}
