package com.pixurvival.core.contentPack;

import java.io.Serializable;

import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentPackIdentifier implements Serializable {

	private static final long serialVersionUID = 1L;

	@Required
	@Length(min = 3)
	private String name = "MyContentPack";

	@Valid
	private Version version = new Version(1, 0);

	public ContentPackIdentifier(String name, int majorVersion, int minorVersion) {
		this.name = name;
		version = new Version(majorVersion, minorVersion);
	}

	public ContentPackIdentifier(ContentPackIdentifier other) {
		name = other.name;
		version = other.version;
	}

	public ContentPackIdentifier(String fileName) {
		int separation = fileName.lastIndexOf("_");
		int extensionIndex = fileName.lastIndexOf('.');
		name = fileName.substring(0, separation);
		version = new Version(fileName.substring(separation + 1, extensionIndex));
	}

	public String fileName() {
		return new StringBuilder().append(name).append("_").append(version).append(".zip").toString();
	}

	public static boolean matchesContentPackFileName(String fileName) {
		return fileName != null && fileName.matches("[a-zA-Z0-9\\-]+_\\d+\\.\\d+\\.zip");
	}

	public static ContentPackIdentifier getIndentifierIfValid(String fileName) {
		if (matchesContentPackFileName(fileName)) {
			ContentPackIdentifier identifier = new ContentPackIdentifier(fileName);
			if (identifier.fileName().equals(fileName)) {
				return identifier;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return name + " " + version;
	}
}