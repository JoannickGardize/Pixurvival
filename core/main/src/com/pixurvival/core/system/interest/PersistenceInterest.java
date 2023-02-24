package com.pixurvival.core.system.interest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public interface PersistenceInterest extends Interest {

    Object getPersistenceData();

    void setPersistenceData(Object data);

    default void save(Output output, Kryo kryo) {
        kryo.writeClassAndObject(output, getPersistenceData());
    }

    default void load(Input input, Kryo kryo) {
        setPersistenceData(kryo.readClassAndObject(input));
    }
}
