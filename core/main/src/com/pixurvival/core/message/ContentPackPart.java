package com.pixurvival.core.message;

import com.pixurvival.core.contentPack.ContentPackIdentifier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "data")
public class ContentPackPart {

    public static final int MAX_PART_LENGTH = 1024;

    private ContentPackIdentifier identifier;
    private int partNumber;
    private int numberOfPart;
    private int length;
    private byte[] data;
}