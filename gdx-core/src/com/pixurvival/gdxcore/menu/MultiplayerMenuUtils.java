package com.pixurvival.gdxcore.menu;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.pixurvival.gdxcore.notificationpush.NotificationPushManager;
import com.pixurvival.gdxcore.util.GeneralSettings;
import com.pixurvival.gdxcore.util.UserDirectory;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MultiplayerMenuUtils {

	public static void setPlayerName(TextField textField) {
		String playerName = UserDirectory.getGeneralSettings().getPlayerName();
		if (playerName == null) {
			playerName = NotificationPushManager.getInstance().getUsername();
		}
		textField.setText(playerName == null ? "Bob" : playerName);
	}

	public static void savePlayerName(TextField textField) {
		GeneralSettings settings = UserDirectory.getGeneralSettings();
		if (!textField.getText().equals(settings.getPlayerName())) {
			settings.setPlayerName(textField.getText());
			UserDirectory.saveGeneralSettings();
		}
	}
}
