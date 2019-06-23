package com.pixurvival.core.contentPack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.representer.Representer;

import com.pixurvival.core.util.FileUtils;

public class ContentPackSerializer {
	public static final String SERIALIZATION_ENTRY_NAME = "contentPack.yml";

	private Yaml yaml;

	private File workingDirectory;

	public ContentPackSerializer(File workingDirectory) {
		this.workingDirectory = workingDirectory;
		Representer representer = new Representer();
		representer.getPropertyUtils().setSkipMissingProperties(true);
		yaml = new Yaml(representer);
		yaml.setBeanAccess(BeanAccess.FIELD);
	}

	public ContentPackSerializer() {
		this(null);
	}

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
			ZipEntry entry = zipFile.getEntry(SERIALIZATION_ENTRY_NAME);
			ContentPack contentPack = null;
			contentPack = yaml.loadAs(zipFile.getInputStream(entry), ContentPack.class);

			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			while (enumeration.hasMoreElements()) {
				entry = enumeration.nextElement();
				if (entry.getName().equals(SERIALIZATION_ENTRY_NAME)) {
					continue;
				}
				contentPack.addResource(entry.getName(), FileUtils.readBytes(zipFile.getInputStream(entry)));
			}

			return contentPack;
		} catch (IOException e) {
			throw new ContentPackException(e);
		}
	}

	public void save(File file, ContentPack contentPack) throws IOException {
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file))) {
			zipOutputStream.putNextEntry(new ZipEntry(SERIALIZATION_ENTRY_NAME));
			yaml.dump(contentPack, new OutputStreamWriter(zipOutputStream));
			for (Entry<String, byte[]> resource : contentPack.getResources().entrySet()) {
				zipOutputStream.putNextEntry(new ZipEntry(resource.getKey()));
				zipOutputStream.write(resource.getValue());
			}
			zipOutputStream.closeEntry();
		}
	}
}
