package com.pixurvival.core.util;

import org.junit.Assert;
import org.junit.Test;

public class DoubleBufferedValueTest {

	@Test
	public void swapTest() {
		DoubleBufferedValue<Object> buffer = new DoubleBufferedValue<>(Object::new);
		Object currentValue = buffer.getCurrentValue();
		Object previousValue = buffer.getPreviousValue();
		buffer.swap();
		Assert.assertSame(currentValue, buffer.getPreviousValue());
		Assert.assertSame(previousValue, buffer.getCurrentValue());
	}
}
