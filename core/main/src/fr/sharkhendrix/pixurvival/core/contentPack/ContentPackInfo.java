package fr.sharkhendrix.pixurvival.core.contentPack;

import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;

@Getter
public class ContentPackInfo {

	@XmlElement(name = "name")
	private String name;
	@XmlElement(name = "uniqueIdentifier")
	private UUID uniqueIdentifier;
	@XmlElement(name = "author")
	private String author;
	@XmlElement(name = "description")
	private String description;
}
