package com.pixurvival.core.util;

import org.junit.Assert;
import org.junit.Test;

import lombok.Getter;

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

		String[] args = { "string = theValue", "integerPrimitive=6", "booleanPrimitive=true", "doubleWrapper=14.5" };

		WrapperClass argsClass = ArgsUtils.readArgs(args, WrapperClass.class);

		Assert.assertEquals("theValue", argsClass.getString());
		Assert.assertEquals(6, argsClass.getIntegerPrimitive());
		Assert.assertTrue(argsClass.isBooleanPrimitive());
		Assert.assertEquals(14.5, argsClass.getDoubleWrapper().doubleValue(), 0.001);
	}
}
