package com.pixurvival.gdxcore.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.CollectionUtils;
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
		Map<String, List<PlayerEntity>> playersByTeam = new HashMap<>();
		World world = PixurvivalGame.getClient().getWorld();
		if (endGameData.getRemainingPlayerIds().length > 0) {
			for (long playerId : endGameData.getRemainingPlayerIds()) {
				PlayerEntity player = world.getPlayerEntities().get(playerId);
				playersByTeam.computeIfAbsent(player.getTeam().getName(), team -> new ArrayList<>()).add(player);
			}
			add(new Label(PixurvivalGame.getString("hud.endGame.survivedPlayers"), PixurvivalGame.getSkin(), "white")).colspan(2);
			row();
			for (Entry<String, List<PlayerEntity>> entry : playersByTeam.entrySet()) {
				StringBuilder sb = new StringBuilder();
				sb.append("[ORANGE]").append(entry.getKey());
				sb.append("[WHITE] : ");
				sb.append(CollectionUtils.toString(entry.getValue()));
				add(new Label(sb.toString(), PixurvivalGame.getSkin(), "white")).colspan(2);
				row();
			}
		}
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

	public void update(Viewport viewport) {
		setPosition(viewport.getWorldWidth() / 4, viewport.getWorldHeight() / 4);
		setSize(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
	}

}
