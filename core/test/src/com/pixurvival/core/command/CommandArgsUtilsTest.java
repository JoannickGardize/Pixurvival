package com.pixurvival.core.command;

import org.junit.Assert;
import org.junit.Test;

import com.pixurvival.core.command.CommandArgsUtils;

public class CommandArgsUtilsTest {

	@Test
	public void splitArgsTest() {
		Assert.assertArrayEquals(new String[] { "my", "name", "is", "John Doe" }, CommandArgsUtils.splitArgs("my  name is \"John Doe\""));
	}
}
