package com.pixurvival.core.contentPack;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PACKAGE)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ContentPackIdentifier implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name = "MyContentPack";
	private Version version = new Version(1, 0);

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
}