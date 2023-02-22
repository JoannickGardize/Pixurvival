package com.pixurvival.contentPackEditor.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FontSize {
    NORMAL(1),
    LARGE(1.5f),
    VERY_LARGE(2f),
    ULTRA_LARGE(3f);

    @Getter
    private float multiplier;
}
