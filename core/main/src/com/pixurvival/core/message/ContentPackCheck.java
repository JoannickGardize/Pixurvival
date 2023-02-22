package com.pixurvival.core.message;

import com.pixurvival.core.contentPack.ContentPackIdentifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentPackCheck {

    private ContentPackIdentifier identifier;
    private byte[] checksum;
}
