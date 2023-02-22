package com.pixurvival.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DoubleBufferedValueTest {

    @Test
    void swapTest() {
        DoubleBufferedValue<Object> buffer = new DoubleBufferedValue<>(Object::new);
        Object currentValue = buffer.getCurrentValue();
        Object previousValue = buffer.getPreviousValue();
        buffer.swap();
        Assertions.assertSame(currentValue, buffer.getPreviousValue());
        Assertions.assertSame(previousValue, buffer.getCurrentValue());
    }
}
