package com.pixurvival.gdxcore;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.I18NBundle;
import com.pixurvival.client.ClientGame;

public class PixurvivalGame extends Game {

	private ClientGame clientGame;
	private AssetManager assetManager;

	@Override
	public void create() {
		clientGame = new ClientGame();
		assetManager = new AssetManager();
		assetManager.load(Assets.I18N_BUNDLE, I18NBundle.class);
		// TODO barre de chargement
		assetManager.finishLoading();
		setScreen(new MainMenuScreen(this));
	}

	public String getString(String key) {
		return assetManager.get(Assets.I18N_BUNDLE, I18NBundle.class).get(key);
	}

	@Override
	public void dispose() {
		assetManager.dispose();
		super.dispose();
	}
}
