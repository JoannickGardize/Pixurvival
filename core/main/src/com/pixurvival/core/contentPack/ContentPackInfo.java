package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
@XmlRootElement(name = "contentPackInfo")
public class ContentPackInfo extends ContentPackIdentifier {

	public static final String XML_FILE_NAME = "contentPackInfo.xml";

	@XmlElement(name = "author")
	private String author;
	@XmlElement(name = "description")
	private String description;
	@XmlElement(name = "dependencies")
	private Dependencies dependencies = new Dependencies();
}
