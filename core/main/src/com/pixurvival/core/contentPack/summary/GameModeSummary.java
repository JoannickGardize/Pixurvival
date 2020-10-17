package com.pixurvival.core.contentPack.summary;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IntegerInterval;
import com.pixurvival.core.contentPack.TranslationKey;
import com.pixurvival.core.contentPack.gameMode.GameMode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameModeSummary {

	private Map<String, String> nameTranslations;

	private IntegerInterval teamNumberInterval = new IntegerInterval();

	private IntegerInterval teamSizeInterval = new IntegerInterval();

	public static GameModeSummary build(ContentPack contentPack, GameMode gameMode) {
		GameModeSummary gameModeSummary = new GameModeSummary();
		gameModeSummary.nameTranslations = new HashMap<>();
		for (Entry<Locale, Properties> translationEntry : contentPack.getTranslations().entrySet()) {
			gameModeSummary.nameTranslations.put(translationEntry.getKey().toLanguageTag(), contentPack.getTranslation(translationEntry.getKey(), gameMode, TranslationKey.NAME));
		}
		gameModeSummary.teamNumberInterval = gameMode.getTeamNumberInterval();
		gameModeSummary.teamSizeInterval = gameMode.getTeamSizeInterval();
		return gameModeSummary;
	}
}
