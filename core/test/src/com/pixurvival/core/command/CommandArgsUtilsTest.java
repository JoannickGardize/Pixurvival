package com.pixurvival.core.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandArgsUtilsTest {

	@Test
	public void splitArgsTest() {
		Assertions.assertArrayEquals(new String[] { "my", "name", "is", "John Doe" }, CommandArgsUtils.splitArgs("my  name is \"John Doe\""));
	}
}
