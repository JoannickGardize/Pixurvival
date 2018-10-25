package com.pixurvival.core.contentPack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.pixurvival.core.util.FileUtils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ContentPackLoader {

	private File workingDirectory;

	public ContentPack load(ContentPackIdentifier identifier) throws ContentPackException {
		String fileName = identifier.fileName();
		File file = new File(workingDirectory, fileName);
		return load(file);
	}

	public ContentPack load(File file) throws ContentPackException {
		if (!file.exists()) {
			throw new ContentPackException(new FileNotFoundException(file.getAbsolutePath()));
		}
		try (ZipFile zipFile = new ZipFile(file)) {
			ZipEntry entry = zipFile.getEntry(ContentPack.SERIALIZATION_ENTRY_NAME);
			ContentPack contentPack = null;
			try (ObjectInputStream ois = new ObjectInputStream(zipFile.getInputStream(entry))) {
				Object o = ois.readObject();
				if (!(o instanceof ContentPack)) {
					throw new ContentPackException("The Object is not a ContentPack !");
				}
				contentPack = (ContentPack) o;
			}

			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			while (enumeration.hasMoreElements()) {
				entry = enumeration.nextElement();
				if (entry.getName().equals(ContentPack.SERIALIZATION_ENTRY_NAME)) {
					continue;
				}
				contentPack.addResource(entry.getName(), FileUtils.readBytes(zipFile.getInputStream(entry)));
			}

			return contentPack;
		} catch (IOException | ClassNotFoundException e) {
			throw new ContentPackException(e);
		}
	}
}
