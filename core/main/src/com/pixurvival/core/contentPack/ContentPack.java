package com.pixurvival.core.contentPack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

	private @NonNull ContentPackFileInfo info;
	private Sprites sprites;
	private List<Tiles> tiles = new ArrayList<>();

	static ContentPack load(RefContext refContext, Unmarshaller unmarshaller, ContentPackFileInfo info)
			throws ContentPackReadException {
		ContentPack contentPack = new ContentPack(info);
		try (ZipFile zipFile = new ZipFile(info.getFile())) {

			AnimationTemplates animationTemplates = (AnimationTemplates) readXmlFile(unmarshaller, zipFile,
					"animationTemplates.xml");
			refContext.addElementSet(AnimationTemplate.class, animationTemplates);
			contentPack.sprites = (Sprites) readXmlFile(unmarshaller, zipFile, "sprites.xml");
			Tiles tiles = (Tiles) readXmlFile(unmarshaller, zipFile, "tiles.xml");
			refContext.addElementSet(Tile.class, tiles);
			refContext.removeCurrentSets();
			refContext.getAdapter(Tile.class).allSets();
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
