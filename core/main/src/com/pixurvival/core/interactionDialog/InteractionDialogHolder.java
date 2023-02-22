package com.pixurvival.core.interactionDialog;

import com.pixurvival.core.team.TeamMember;

public interface InteractionDialogHolder extends TeamMember {

    InteractionDialog getInteractionDialog();

    void setInteractionDialog(InteractionDialog interactionDialog);
}
