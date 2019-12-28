package com.pixurvival.client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.message.ContentPackPart;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ContentPackDownloader {

	private Map<ContentPackIdentifier, OutputStream> writingMap = new HashMap<>();
	private @NonNull ClientGame client;

	public void accept(ContentPackPart contentPackPart) {
		OutputStream output = null;
		try {
			output = writingMap.get(contentPackPart.getIdentifier());
			if (output == null) {
				File file = new File(client.getContentPackSerialization().getWorkingDirectory(), contentPackPart.getIdentifier().fileName());
				Log.debug("Creating file : " + file.getAbsolutePath());
				output = new BufferedOutputStream(new FileOutputStream(file));
				writingMap.put(contentPackPart.getIdentifier(), output);
			}
			output.write(contentPackPart.getData(), 0, contentPackPart.getLength());
			if (contentPackPart.getPartNumber() == contentPackPart.getNumberOfPart() - 1) {
				output.flush();
				output.close();
				writingMap.remove(contentPackPart.getIdentifier());
				client.notifyContentPackAvailable(contentPackPart.getIdentifier());
			}
		} catch (IOException e) {
			Log.error("Error when writing content pack file.", e);
			writingMap.remove(contentPackPart.getIdentifier());
		}
	}
}
