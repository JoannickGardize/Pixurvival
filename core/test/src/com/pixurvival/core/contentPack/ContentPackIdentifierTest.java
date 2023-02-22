package com.pixurvival.core.contentPack;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ContentPackIdentifierTest {

    @Test
    public void matchesContentPackFileNameTest() {
        Assertions.assertTrue(ContentPackIdentifier.matchesContentPackFileName("AlekhAZE-56_1.0.zip"));
        Assertions.assertTrue(ContentPackIdentifier.matchesContentPackFileName("57---zrf-AZE_500.001.zip"));
        Assertions.assertFalse(ContentPackIdentifier.matchesContentPackFileName("_1.1.zip"));
        Assertions.assertFalse(ContentPackIdentifier.matchesContentPackFileName("kj_1.zip"));
        Assertions.assertFalse(ContentPackIdentifier.matchesContentPackFileName("kjk__1.0.zip"));
        Assertions.assertFalse(ContentPackIdentifier.matchesContentPackFileName("AZE%d_2.0.zip"));
        Assertions.assertFalse(ContentPackIdentifier.matchesContentPackFileName("ABC_1.0.1.zip"));
        Assertions.assertFalse(ContentPackIdentifier.matchesContentPackFileName("qsd_1.0.png"));
        Assertions.assertFalse(ContentPackIdentifier.matchesContentPackFileName("a_1.0zip"));
        Assertions.assertFalse(ContentPackIdentifier.matchesContentPackFileName("truc.zip"));
    }

    @Test
    public void getIndentifierIfValidTest() {
        Assertions.assertNull(ContentPackIdentifier.getIndentifierBasedOnFileName("AlekhAZE-561.0.zip"));
        Assertions.assertNull(ContentPackIdentifier.getIndentifierBasedOnFileName("AlekhAZE-56_01.0.zip"));
        Assertions.assertNotNull(ContentPackIdentifier.getIndentifierBasedOnFileName("AlekhAZE-56_1.0.zip"));
    }
}
