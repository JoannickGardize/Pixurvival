package com.pixurvival.core.contentPack;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContentPack {

	private @NonNull File file;
	private Sprites sprites;

	static ContentPack load(RefContext refContext, Unmarshaller unmarshaller, File file)
			throws ContentPackReadException {
		ContentPack contentPack = new ContentPack(file);
		try (ZipFile zipFile = new ZipFile(file)) {

			AnimationTemplates animationTemplates = (AnimationTemplates) readXmlFile(unmarshaller, zipFile,
					"animationTemplates.xml");
			refContext.addElementSet(AnimationTemplate.class, animationTemplates);
			contentPack.sprites = (Sprites) readXmlFile(unmarshaller, zipFile, "sprites.xml");
			refContext.removeCurrentSets();
			return contentPack;
		} catch (Exception e) {
			throw new ContentPackReadException(e);
		}
	}

	private static Object readXmlFile(Unmarshaller unmarshaller, ZipFile zipFile, String name)
			throws ContentPackReadException, JAXBException, IOException {
		ZipEntry entry = zipFile.getEntry(name);
		if (entry == null) {
			throw new ContentPackReadException("Missing file " + name);
		}
		return unmarshaller.unmarshal(zipFile.getInputStream(entry));
	}

	public static void main(String[] args) throws ContentPackException {
		ContentPacksContext c = new ContentPacksContext(new File("contentPacks"));
		c.load(new ContentPackIdentifier("Vanilla", new Version("0.1"),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b")));
		System.out.println("ok");
	}
}
