package fr.sharkhendrix.pixurvival.core.contentPack;

import java.io.File;

import lombok.Getter;

@Getter
public class ContentPackFileInfo extends ContentPackInfo {

	private File file;

	public ContentPackFileInfo(ContentPackInfo info, File file) {
		setAuthor(info.getAuthor());
		setDependencies(info.getDependencies());
		setDescription(info.getDescription());
		setName(info.getName());
		setUniqueIdentifier(info.getUniqueIdentifier());
		setVersion(info.getVersion());
		this.file = file;
	}
}
