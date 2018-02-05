package fr.sharkhendrix.pixurvival.core.contentPack;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

public class XmlParseTest {

	@Test
	public void animationTemplatesParse() throws JAXBException {
		File file = new File("animationTemplates.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(AnimationTemplates.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		AnimationTemplates customer = (AnimationTemplates) jaxbUnmarshaller.unmarshal(file);
	}
}
