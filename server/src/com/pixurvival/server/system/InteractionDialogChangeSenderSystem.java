package com.pixurvival.server.system;

import java.util.Map;
import java.util.Set;

import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.UpdateInteractionDialog;
import com.pixurvival.core.system.GameSystem;
import com.pixurvival.core.system.Inject;
import com.pixurvival.core.system.interest.InteractionDialogInterest;
import com.pixurvival.server.PlayerGameSession;

import lombok.Setter;

@Setter
public class InteractionDialogChangeSenderSystem implements GameSystem, InteractionDialogInterest {

	@Inject
	private Map<Long, Set<PlayerGameSession>> sessionsByEntities;

	@Override
	public void dialogOpened(PlayerEntity playerEntity, InteractionDialog interactionDialog) {
		sendUpdate(playerEntity, interactionDialog);
	}

	@Override
	public void dialogClosed(PlayerEntity playerEntity, InteractionDialog interactionDialog) {
		sendUpdate(playerEntity, null);
	}

	@Override
	public void viewingDialogChanged(InteractionDialog interactionDialog) {
		interactionDialog.getViewers().forEach(p -> sendUpdate(p, interactionDialog));
	}

	private void sendUpdate(PlayerEntity p, InteractionDialog interactionDialog) {
		Set<PlayerGameSession> sessions = sessionsByEntities.get(p.getId());
		if (sessions != null) {
			UpdateInteractionDialog updateInteractionDialog = new UpdateInteractionDialog(interactionDialog);
			sessions.forEach(s -> s.getConnection().sendTCP(updateInteractionDialog));
		}
	}

}
