package com.pixurvival.server.system;

import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.UpdateInteractionDialog;
import com.pixurvival.core.system.GameSystem;
import com.pixurvival.core.system.Inject;
import com.pixurvival.core.system.interest.InteractionDialogInterest;
import com.pixurvival.core.system.interest.WorldUpdateInterest;
import com.pixurvival.server.PlayerGameSession;
import lombok.Setter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Setter
public class InteractionDialogChangeSenderSystem implements GameSystem, InteractionDialogInterest, WorldUpdateInterest {

    @Inject
    private Map<Long, Set<PlayerGameSession>> sessionsByEntities;

    private Set<InteractionDialog> notifiedDialogs = new HashSet<>();

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
        notifiedDialogs.add(interactionDialog);
    }

    private void sendUpdate(PlayerEntity p, InteractionDialog interactionDialog) {
        Set<PlayerGameSession> sessions = sessionsByEntities.get(p.getId());
        if (sessions != null) {
            // TODO The last one per player only
            sessions.forEach(s -> s.getConnection().sendTCP(new UpdateInteractionDialog(interactionDialog)));
        }
    }

    @Override
    public void update(float deltaTime) {
        notifiedDialogs.forEach(d -> d.getViewers().forEach(p -> sendUpdate(p, d)));
        notifiedDialogs.clear();
    }
}
