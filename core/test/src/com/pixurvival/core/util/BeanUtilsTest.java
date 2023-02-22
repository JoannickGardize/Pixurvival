package com.pixurvival.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BeanUtilsTest {

    @Test
    public void upperToCamelCaseTest() {
        String s = "HELLO_THE_WORLD";
        String s2 = CaseUtils.upperToCamelCase(s);
        Assertions.assertEquals("helloTheWorld", s2);
    }

    @Test
    public void camelToUpperCaseTest() {
        String s = "helloTheWorld";
        String s2 = CaseUtils.camelToUpperCase(s);
        Assertions.assertEquals("HELLO_THE_WORLD", s2);
    }
}
