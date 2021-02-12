package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.core.interactionDialog.InventoryInteractionDialog;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.playerRequest.DialogInteractionActionRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.WorldScreen;
import com.pixurvival.gdxcore.menu.MenuButton;

import lombok.Getter;

public class InteractionDialogUI extends Table {

	private static final @Getter InteractionDialogUI instance = new InteractionDialogUI();

	private Actor actualContent;

	private InteractionDialogUI() {
		setVisible(false);
		setBackground(PixurvivalGame.getSkin().get("panel", Drawable.class));
		defaults().fill().pad(2);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		if (myPlayer == null) {
			setVisible(false);
		} else {
			InteractionDialog dialog = myPlayer.getInteractionDialog();
			if (dialog == null) {
				actualContent = null;
				setVisible(false);
			} else if (dialog instanceof InventoryInteractionDialog) {
				buildInventoryInteractionDialog((InventoryInteractionDialog) dialog);
			}
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha * 0.75f);
	}

	public void sizeAndPosition() {
		if (actualContent instanceof InventoryTable) {
			sizeAndPositionInventoryInteractionDialog();
		}
	}

	private void buildInventoryInteractionDialog(InventoryInteractionDialog dialog) {
		if (actualContent instanceof InventoryTable && ((InventoryTable) actualContent).getInventory() == dialog.getInventory()) {
			return;
		}
		actualContent = new InventoryTable(dialog.getInventory(), 8);
		clearChildren();
		add(actualContent).colspan(3).expandY().fill();
		row();
		add(new MenuButton("inventoryDialog.fill", () -> PixurvivalGame.getClient().sendAction(new DialogInteractionActionRequest(InventoryInteractionDialog.FILL_ACTION_INDEX, false)))).width(70)
				.padLeft(5);
		add(new MenuButton("inventoryDialog.empty", () -> PixurvivalGame.getClient().sendAction(new DialogInteractionActionRequest(InventoryInteractionDialog.EMPTY_ACTION_INDEX, false)))).width(70);
		add().expandX();

		sizeAndPositionInventoryInteractionDialog();
	}

	private void sizeAndPositionInventoryInteractionDialog() {
		Screen s = PixurvivalGame.getInstance().getScreen();
		if (!(s instanceof WorldScreen)) {
			return;
		}
		InventoryUI inventoryUI = ((WorldScreen) s).getInventoryUI();
		((InventoryTable) actualContent).setCellsPrefSize(inventoryUI.getInventoryTable().getActualCellSize());
		pack();
		setPosition(inventoryUI.getX() + inventoryUI.getWidth() + 5,
				inventoryUI.getY() + inventoryUI.getInventoryTable().getY() + inventoryUI.getInventoryTable().getHeight() / 2 - actualContent.getHeight() / 2 - actualContent.getY());
		setVisible(true);
	}

}
