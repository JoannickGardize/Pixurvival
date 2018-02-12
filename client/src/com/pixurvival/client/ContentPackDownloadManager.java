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

public class ContentPackDownloadManager {

	private Map<ContentPackIdentifier, OutputStream> writingMap = new HashMap<>();

	public void accept(ContentPackPart contentPackPart) {
		OutputStream output = null;
		try {
			output = writingMap.get(contentPackPart.getIdentifier());
			if (output == null) {
				File file = new File("contentPacks/" + contentPackPart.getIdentifier().buildFileName());
				Log.debug("Creating file : " + file.getAbsolutePath());
				output = new BufferedOutputStream(new FileOutputStream(file));
				writingMap.put(contentPackPart.getIdentifier(), output);
			}
			Log.debug("Write");
			output.write(contentPackPart.getData());
			if (contentPackPart.getPartNumber() == contentPackPart.getNumberOfPart() - 1) {
				Log.debug("end write file");
				output.flush();
				output.close();
				writingMap.remove(contentPackPart.getIdentifier());
			}
		} catch (IOException e) {
			Log.error("Error when writing content pack file.", e);
			if (output != null) {
				try {
					output.close();
				} catch (IOException e1) {
				}
			}
			writingMap.remove(contentPackPart.getIdentifier());
		}
	}
}
