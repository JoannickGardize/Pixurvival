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
import com.pixurvival.core.contentPack.gameMode.role.Roles.SelectionMode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameModeSummary {

	private Map<String, String> nameTranslations;

	private Map<String, String> descriptionTranslations;

	private IntegerInterval teamNumberInterval = new IntegerInterval();

	private IntegerInterval teamSizeInterval = new IntegerInterval();

	private RoleSummary[] roleSummaries;

	public GameModeSummary(ContentPack contentPack, GameMode gameMode) {
		nameTranslations = new HashMap<>();
		descriptionTranslations = new HashMap<>();
		for (Entry<Locale, Properties> translationEntry : contentPack.getTranslations().entrySet()) {
			String languageTag = translationEntry.getKey().toLanguageTag();
			nameTranslations.put(languageTag, contentPack.getTranslation(translationEntry.getKey(), gameMode, TranslationKey.NAME));
			descriptionTranslations.put(languageTag, contentPack.getTranslation(translationEntry.getKey(), gameMode, TranslationKey.DESCRIPTION));
		}
		teamNumberInterval = gameMode.getTeamNumberInterval();
		teamSizeInterval = gameMode.getTeamSizeInterval();
		if (gameMode.getRoles() != null && gameMode.getRoles().getSelectionMode() == SelectionMode.LOBBY) {
			roleSummaries = new RoleSummary[gameMode.getRoles().getRoles().size()];
			for (int i = 0; i < roleSummaries.length; i++) {
				roleSummaries[i] = new RoleSummary(gameMode.getRoles().getRoles().get(i));
			}
		}
	}
}
