package com.pixurvival.core.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
enum BackwardCompatibility {
    NONE(false, false),
    CONTENT_PACK_ONLY(true, false),
    FULL(true, true);

    private boolean contentPacks;
    private boolean saves;
}
