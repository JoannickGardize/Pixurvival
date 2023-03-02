package com.pixurvival.core.contentPack.serialization;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.serialization.io.StoreInput;
import com.pixurvival.core.contentPack.serialization.io.StoreOutput;

public interface ContentPackSerializationPlugin {

    void read(ContentPack contentPack, StoreInput input);

    void write(ContentPack contentPack, StoreOutput output);
}
