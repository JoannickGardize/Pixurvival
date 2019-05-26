package com.pixurvival.server.console;

import org.junit.Assert;
import org.junit.Test;

public class ServerConsoleTest {

	@Test
	public void splitArgsTest() {
		Assert.assertArrayEquals(new String[] { "my", "name", "is", "John Doe" }, ConsoleArgsUtils.splitArgs("my  name is \"John Doe\""));
	}
}
