package com.pixurvival.gdxcore.ui.interactionDialog;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.pixurvival.core.interactionDialog.FactoryInteractionDialog;
import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.core.interactionDialog.InventoryInteractionDialog;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.WorldScreen;
import com.pixurvival.gdxcore.ui.InventoryUI;

import lombok.Getter;
import lombok.SneakyThrows;

public class InteractionDialogUI extends Table {

	private static final @Getter InteractionDialogUI instance = new InteractionDialogUI();

	private Map<Class<? extends InteractionDialog>, Supplier<DialogContent>> contents = new IdentityHashMap<>();
	private DialogContent actualContent;

	private InteractionDialogUI() {
		setVisible(false);
		setBackground(PixurvivalGame.getSkin().get("panel", Drawable.class));
		defaults().expand().fill().pad(2);
		contents.put(InventoryInteractionDialog.class, InventoryDialogContent::new);
		contents.put(FactoryInteractionDialog.class, FactoryDialogContent::new);
	}

	@SneakyThrows
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
			} else {
				setVisible(true);
				if (actualContent == null || actualContent.getDialogType() != dialog.getClass()) {
					actualContent = contents.get(dialog.getClass()).get();
					clearChildren();
					add(actualContent);
				}
				if (actualContent.build(dialog)) {
					sizeAndPosition();
				}
			}
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha * 0.75f);
	}

	public void sizeAndPosition() {
		if (actualContent == null) {
			return;
		}
		Screen s = PixurvivalGame.getInstance().getScreen();
		if (!(s instanceof WorldScreen)) {
			return;
		}
		InventoryUI inventoryUI = ((WorldScreen) s).getInventoryUI();
		actualContent.forEachInventories(i -> i.setCellsPrefSize(inventoryUI.getInventoryTable().getActualCellSize()));
		actualContent.invalidate();
		actualContent.validate();
		pack();
		Actor alignActor = actualContent.getAlignActor();
		float offsetY = alignActor == actualContent ? 0 : alignActor.getY() + actualContent.getY();

		setPosition(inventoryUI.getX() + inventoryUI.getWidth() + 5,
				inventoryUI.getY() + inventoryUI.getInventoryTable().getY() + inventoryUI.getInventoryTable().getHeight() / 2 - alignActor.getHeight() / 2 - offsetY);
	}
}
