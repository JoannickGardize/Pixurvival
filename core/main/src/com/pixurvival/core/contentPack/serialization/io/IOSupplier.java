package com.pixurvival.core.contentPack.serialization.io;

import java.io.IOException;

@FunctionalInterface
public interface IOSupplier<T> {

    T get() throws IOException;
}
