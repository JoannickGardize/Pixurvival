package com.pixurvival.core.util;

import org.junit.Assert;
import org.junit.Test;

public class BeanUtilsTest {

	@Test
	public void upperToCamelCase() {
		String s = "HELLO_THE_WORLD";
		String s2 = BeanUtils.upperToCamelCase(s);
		Assert.assertEquals("helloTheWorld", s2);
	}

	@Test
	public void camelToUpperCaseCase() {
		String s = "helloTheWorld";
		String s2 = BeanUtils.camelToUpperCase(s);
		Assert.assertEquals("HELLO_THE_WORLD", s2);
	}
}
