package com.pixurvival.gdxcore.ui.interactionDialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.gdxcore.ui.InventoryTable;

import java.util.function.Consumer;

public abstract class DialogContent extends Table {

    // DialogContent() {
    // setFillParent(true);
    // }

    /**
     * @param dialog
     * @return true if rebuild has been made
     */
    public abstract boolean build(InteractionDialog dialog);

    public abstract void forEachInventories(Consumer<InventoryTable> action);

    public abstract Actor getAlignActor();

    public abstract Class<? extends InteractionDialog> getDialogType();
}
