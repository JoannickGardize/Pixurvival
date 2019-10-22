package com.pixurvival.core.contentPack.serialization;

import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.pixurvival.core.contentPack.ContentPack;

public interface ContentPackSerializerPlugin {

	void read(ContentPack contentPack, ZipFile zipFile);

	void write(ContentPack contentPack, ZipOutputStream zipOutputStream);
}
