package com.pixurvival.core.contentPack.summary;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContentPackSummary {

    private ContentPackIdentifier identifier;
    private GameModeSummary[] gameModeSummaries;
    private String releaseVersion;

    private transient boolean directoryMode;

    public ContentPackSummary(ContentPack contentPack) {
        identifier = contentPack.getIdentifier();
        gameModeSummaries = new GameModeSummary[contentPack.getGameModes().size()];
        for (int i = 0; i < contentPack.getGameModes().size(); i++) {
            gameModeSummaries[i] = new GameModeSummary(contentPack, contentPack.getGameModes().get(i));
        }
        releaseVersion = contentPack.getReleaseVersion();
    }

    @Override
    public String toString() {
        return identifier.toString();
    }
}
