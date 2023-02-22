package com.pixurvival.gdxcore.ui.interactionDialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.core.interactionDialog.InventoryInteractionDialog;
import com.pixurvival.core.message.playerRequest.DialogInteractionActionRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.menu.MenuButton;
import com.pixurvival.gdxcore.ui.InventoryTable;

import java.util.function.Consumer;

public class InventoryDialogContent extends DialogContent {

    private InventoryTable actualInventoryTable;

    public InventoryDialogContent() {
        defaults().pad(2);
    }

    @Override
    public boolean build(InteractionDialog dialog) {
        InventoryInteractionDialog invDialog = (InventoryInteractionDialog) dialog;
        if (actualInventoryTable != null && actualInventoryTable.getInventory() == invDialog.getInventory()) {
            return false;
        }
        actualInventoryTable = new InventoryTable(invDialog.getInventory(), 8);
        clearChildren();
        add(actualInventoryTable).colspan(3).expandY().fill();
        row();
        add(new MenuButton("inventoryDialog.fill", () -> PixurvivalGame.getClient().sendAction(new DialogInteractionActionRequest(InventoryInteractionDialog.FILL_ACTION_INDEX, false)))).width(70)
                .padLeft(5);
        add(new MenuButton("inventoryDialog.empty", () -> PixurvivalGame.getClient().sendAction(new DialogInteractionActionRequest(InventoryInteractionDialog.EMPTY_ACTION_INDEX, false)))).width(70);
        add().expandX();
        return true;
    }

    @Override
    public void forEachInventories(Consumer<InventoryTable> action) {
        action.accept(actualInventoryTable);
    }

    @Override
    public Actor getAlignActor() {
        return actualInventoryTable;
    }

    @Override
    public Class<? extends InteractionDialog> getDialogType() {
        return InventoryInteractionDialog.class;
    }
}
