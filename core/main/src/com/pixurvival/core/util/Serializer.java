package com.pixurvival.core.util;

import java.nio.ByteBuffer;

public interface Serializer<T> {

    void write(ByteBuffer buffer, T object);

    T read(ByteBuffer buffer);
}
