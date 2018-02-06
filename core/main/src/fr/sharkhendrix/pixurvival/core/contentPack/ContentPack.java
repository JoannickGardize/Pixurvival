package fr.sharkhendrix.pixurvival.core.contentPack;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBContext;
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

	public static ContentPack read(File file) throws ContentPackReadException {
		try {
			ContentPack contentPack = new ContentPack(file);
			ZipFile zipFile = new ZipFile(file);

			JAXBContext context = JAXBContext.newInstance(AnimationTemplates.class, Sprites.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			AnimationTemplates animationTemplates = (AnimationTemplates) readXmlFile(unmarshaller, zipFile,
					"animationTemplates.xml");
			unmarshaller.setAdapter(new AnimationTemplateRefAdapter(animationTemplates));
			contentPack.sprites = (Sprites) readXmlFile(unmarshaller, zipFile, "sprites.xml");
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

	public static void main(String[] args) throws ContentPackReadException {
		File test = new File("bidule");
		test.mkdir();
		ContentPack.read(new File("vanilla.zip"));
	}
}
