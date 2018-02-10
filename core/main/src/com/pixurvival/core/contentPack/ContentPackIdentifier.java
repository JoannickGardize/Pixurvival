package com.pixurvival.core.contentPack;

import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PACKAGE)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContentPackIdentifier {
	@XmlElement(name = "name", required = true)
	private String name;
	@XmlElement(name = "version", required = true)
	private Version version;
	@XmlElement(name = "uniqueIdentifier", required = true)
	private UUID uniqueIdentifier;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((uniqueIdentifier == null) ? 0 : uniqueIdentifier.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ContentPackIdentifier))
			return false;
		ContentPackIdentifier other = (ContentPackIdentifier) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (uniqueIdentifier == null) {
			if (other.uniqueIdentifier != null)
				return false;
		} else if (!uniqueIdentifier.equals(other.uniqueIdentifier))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

}