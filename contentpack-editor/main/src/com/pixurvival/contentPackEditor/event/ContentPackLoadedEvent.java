package com.pixurvival.contentPackEditor.event;

import com.pixurvival.core.contentPack.ContentPack;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContentPackLoadedEvent extends Event {

    private ContentPack contentPack;
}
