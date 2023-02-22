package com.pixurvival.core.system.interest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public interface PersistenceInterest extends Interest {

    Object getData();

    void setData(Object data);

    default void save(Output output, Kryo kryo) {
        kryo.writeClassAndObject(output, getData());
    }

    default void load(Input input, Kryo kryo) {
        setData(kryo.readClassAndObject(input));
    }
}
