package com.pixurvival.core.util;

import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArgsUtilsTest {

    @Getter
    public static class WrapperClass {
        private String string;
        private int integerPrimitive;
        private boolean booleanPrimitive;
        private Double doubleWrapper;
    }

    @Test
    public void readArgsTest() {

        String[] args = {"string = theValue", "integerPrimitive=6", "booleanPrimitive=true", "doubleWrapper=14.5"};

        WrapperClass argsClass = ArgsUtils.readArgs(args, WrapperClass.class);

        Assertions.assertEquals("theValue", argsClass.getString());
        Assertions.assertEquals(6, argsClass.getIntegerPrimitive());
        Assertions.assertTrue(argsClass.isBooleanPrimitive());
        Assertions.assertEquals(14.5, argsClass.getDoubleWrapper().doubleValue(), 0.001);
    }
}
