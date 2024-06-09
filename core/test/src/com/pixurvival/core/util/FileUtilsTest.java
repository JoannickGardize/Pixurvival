package com.pixurvival.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class FileUtilsTest {

    private static Stream<Arguments> fileExtensionOfSource() {
        return Stream.of(Arguments.of("wav", "truc.wav"),
                Arguments.of("", "truc"),
                Arguments.of("wav", "truc.bidule.wav")
        );
    }

    @ParameterizedTest
    @MethodSource("fileExtensionOfSource")
    void fileExtensionOfTest(String expected, String fileName) {
        Assertions.assertEquals(expected, FileUtils.fileExtensionOf(fileName));
    }

}
