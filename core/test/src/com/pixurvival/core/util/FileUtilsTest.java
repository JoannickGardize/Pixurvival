package com.pixurvival.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

    @Test
    void fileExtensionOfTest() {
        Assertions.assertEquals("wav", FileUtils.fileExtensionOf("truc.wav"));
        Assertions.assertEquals("", FileUtils.fileExtensionOf("truc"));
    }

}
