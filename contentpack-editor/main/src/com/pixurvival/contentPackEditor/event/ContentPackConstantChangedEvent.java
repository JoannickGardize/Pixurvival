package com.pixurvival.contentPackEditor.event;

import com.pixurvival.core.contentPack.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContentPackConstantChangedEvent extends Event {

    private Constants constants;
}
