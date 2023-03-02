package com.pixurvival.core.contentPack.serialization.io;

import java.io.IOException;

@FunctionalInterface
public interface InputEntryConsumer {
    void apply(String name) throws IOException;
}
