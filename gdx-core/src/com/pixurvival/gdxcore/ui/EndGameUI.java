package com.pixurvival.gdxcore.ui;

import java.util.function.Supplier;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.WorldScreen;
import com.pixurvival.gdxcore.menu.MainMenuScreen;
import com.pixurvival.gdxcore.ui.tooltip.RepresenterUtils;

public class EndGameUI extends UIWindow {

	public EndGameUI() {
		super("endGame");
		setVisible(false);
		setModal(true);

	}

	public void show(EndGameData endGameData) {
		clearChildren();
		RepresenterUtils.appendLabelledRow(this, "hud.endGame.survivedTime", RepresenterUtils.formatHoursMinutesSecondes(endGameData.getTime()));
		World world = PixurvivalGame.getClient().getWorld();
		buildPlayerGroup(world, endGameData.getPlayerWonIds(), () -> "[GREEN]" + PixurvivalGame.getString("hud.endGame.victory"));
		row();
		buildPlayerGroup(world, endGameData.getPlayerLostIds(), () -> "[RED]" + PixurvivalGame.getString("hud.endGame.defeat"));
		row();
		add().colspan(2).expand();
		TextButton okButton = new TextButton(PixurvivalGame.getString("generic.ok"), PixurvivalGame.getSkin());

		okButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setVisible(false);
				if (PixurvivalGame.getInstance().getScreen() instanceof WorldScreen) {
					PixurvivalGame.setScreen(MainMenuScreen.class);
				}
			}
		});
		row();
		add(okButton).colspan(2).expand();
		setVisible(true);
	}

	private void buildPlayerGroup(World world, long[] playerIds, Supplier<String> titleSupplier) {
		if (playerIds.length > 0) {
			add(new Label(titleSupplier.get(), PixurvivalGame.getSkin(), "white")).colspan(2);
			row();
			StringBuilder sb = new StringBuilder();
			String separator = "";
			for (long playerId : playerIds) {
				PlayerEntity player = world.getPlayerEntities().get(playerId);
				sb.append(separator);
				sb.append(player.getName());
				if (player.getRole() != null) {
					sb.append(" (" + player.getRole().getName() + ")");
				}
				separator = ", ";
			}
			add(new Label(sb.toString(), PixurvivalGame.getSkin(), "white")).colspan(2);
		}
	}

	public void update(Viewport viewport) {
		setPosition(viewport.getWorldWidth() / 4, viewport.getWorldHeight() / 4);
		setSize(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
	}

}
