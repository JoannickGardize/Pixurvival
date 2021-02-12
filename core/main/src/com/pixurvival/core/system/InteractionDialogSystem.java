package com.pixurvival.core.system;

import java.util.ArrayList;
import java.util.Collection;

import com.pixurvival.core.ActionPreconditions;
import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.core.interactionDialog.InteractionDialogChangeListener;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.system.interest.InteractionDialogInterest;
import com.pixurvival.core.system.interest.InteractionDialogRequestInterest;
import com.pixurvival.core.system.interest.InterestSubscription;
import com.pixurvival.core.system.interest.WorldUpdateInterest;

import lombok.Setter;

@Setter
public class InteractionDialogSystem implements GameSystem, InteractionDialogRequestInterest, WorldUpdateInterest, InteractionDialogChangeListener {

	@Inject
	private InterestSubscription<InteractionDialogInterest> interactionDialogSubscrition;

	private Collection<PlayerEntity> viewingPlayers = new ArrayList<>();

	@Override
	public void openDialogRequest(PlayerEntity playerEntity, InteractionDialog interactionDialog) {
		if (ActionPreconditions.checkInteractionDistance(playerEntity, interactionDialog.getOwner())) {
			open(playerEntity, interactionDialog);
			viewingPlayers.add(playerEntity);
		}
	}

	@Override
	public void closeDialogRequest(PlayerEntity playerEntity) {
		if (closeCurrent(playerEntity)) {
			viewingPlayers.remove(playerEntity);
		}
	}

	@Override
	public void interactDialogRequest(PlayerEntity playerEntity, int index, boolean splitMode) {
		if (canInteractWithDialog(playerEntity)) {
			playerEntity.getInteractionDialog().interact(playerEntity, index, splitMode);
		}
	}

	@Override
	public void update(float deltaTime) {
		viewingPlayers.removeIf(player -> {
			if (!canInteractWithDialog(player)) {
				closeCurrent(player);
				return true;
			} else {
				return false;
			}
		});
	}

	private void open(PlayerEntity playerEntity, InteractionDialog interactionDialog) {
		if (playerEntity.getInteractionDialog() == interactionDialog) {
			return;
		}
		if (!closeCurrent(playerEntity)) {
			viewingPlayers.add(playerEntity);
		}
		if (interactionDialog.viewerCount() == 0) {
			interactionDialog.addListener(this);
		}
		interactionDialog.addViewer(playerEntity);
		playerEntity.setInteractionDialog(interactionDialog);
		interactionDialogSubscrition.forEach(i -> i.dialogOpened(playerEntity, interactionDialog));
	}

	private boolean closeCurrent(PlayerEntity playerEntity) {
		if (playerEntity.getInteractionDialog() != null) {
			InteractionDialog dialog = playerEntity.getInteractionDialog();
			dialog.removeViewer(playerEntity);
			if (dialog.viewerCount() == 0) {
				dialog.removeListener(this);
			}
			playerEntity.setInteractionDialog(null);
			interactionDialogSubscrition.forEach(i -> i.dialogClosed(playerEntity, dialog));
			return true;
		} else {
			return false;
		}
	}

	private boolean canInteractWithDialog(PlayerEntity playerEntity) {
		return playerEntity.isAlive() && playerEntity.getInteractionDialog() != null && (playerEntity.getInteractionDialog().getOwner() == null
				|| playerEntity.getInteractionDialog().getOwner().isAlive() && ActionPreconditions.checkInteractionDistance(playerEntity, playerEntity.getInteractionDialog().getOwner()));
	}

	@Override
	public void changed(InteractionDialog interactionDialog) {
		interactionDialogSubscrition.forEach(i -> i.viewingDialogChanged(interactionDialog));
	}
}
