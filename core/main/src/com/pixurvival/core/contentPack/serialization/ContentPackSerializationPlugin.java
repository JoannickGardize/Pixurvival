package com.pixurvival.core.contentPack.serialization;

import com.pixurvival.core.contentPack.ContentPack;

import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public interface ContentPackSerializationPlugin {

    void read(ContentPack contentPack, ZipFile zipFile);

    void write(ContentPack contentPack, ZipOutputStream zipOutputStream);
}
