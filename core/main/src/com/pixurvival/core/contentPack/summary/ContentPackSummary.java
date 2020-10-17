package com.pixurvival.core.contentPack.summary;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackIdentifier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentPackSummary {

	private ContentPackIdentifier identifier;
	private GameModeSummary[] gameModeSummaries;
	private String releaseVersion;

	public static ContentPackSummary build(ContentPack contentPack) {
		ContentPackSummary contentPackSummary = new ContentPackSummary();
		contentPackSummary.setIdentifier(contentPack.getIdentifier());
		contentPackSummary.gameModeSummaries = new GameModeSummary[contentPack.getGameModes().size()];
		for (int i = 0; i < contentPack.getGameModes().size(); i++) {
			contentPackSummary.gameModeSummaries[i] = GameModeSummary.build(contentPack, contentPack.getGameModes().get(i));
		}
		contentPackSummary.releaseVersion = contentPack.getReleaseVersion();
		return contentPackSummary;
	}

	@Override
	public String toString() {
		return identifier.toString();
	}
}
