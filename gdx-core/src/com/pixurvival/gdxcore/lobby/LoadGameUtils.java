package com.pixurvival.gdxcore.lobby;

import com.pixurvival.core.LoadGameException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.util.ReleaseVersion;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.menu.MessageWindow;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LoadGameUtils {

	public static void handleLoadGameError(LoadGameException e, MessageWindow errorWindow) {
		String message;
		switch (e.getReason()) {
		case NOT_PLAYABLE_IN_SOLO:
			message = PixurvivalGame.getString("loadGameError.notPlayableInSolo");
			break;
		case PARSE_EXCEPTION:
			message = PixurvivalGame.getString("loadGameError.parseException", e.getArgs());
			break;
		case WRONG_CONTENT_PACK_VERSION:
			message = PixurvivalGame.getString("loadGameError.wrongVersion", ReleaseVersion.displayNameOf((ReleaseVersion) e.getArgs()[0]),
					ReleaseVersion.displayNameOf((ReleaseVersion) e.getArgs()[1]));
			break;
		case NOT_SAME_CONTENT_PACK:
			message = PixurvivalGame.getString("loadGameError.notSameContentPack", ((ContentPackIdentifier) e.getArgs()[0]).fileName());
			break;
		case WRONG_GAME_VERSION:
			message = PixurvivalGame.getString("loadGameError.wrongGameVersion", ReleaseVersion.displayNameOf((ReleaseVersion) e.getArgs()[0]),
					ReleaseVersion.displayNameOf((ReleaseVersion) e.getArgs()[1]));
			break;
		case OTHER:
			message = (String) e.getArgs()[0];
			break;
		default:
			message = "Unknown error";
			break;

		}
		errorWindow.getContentLabel().setText(message);
		errorWindow.setVisible(true);
	}
}
