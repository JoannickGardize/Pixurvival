package fr.sharkhendrix.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

@Getter
public class Dependency extends ContentPackIdentifier {
	@XmlAttribute(name = "ref")
	private String ref;
}
