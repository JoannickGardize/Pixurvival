package com.pixurvival.core.message;

import com.pixurvival.core.contentPack.ContentPackIdentifier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContentPackRequest {
    private ContentPackIdentifier identifier;
}
