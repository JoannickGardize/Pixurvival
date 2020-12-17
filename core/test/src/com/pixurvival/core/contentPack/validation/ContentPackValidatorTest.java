package com.pixurvival.core.contentPack.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pixurvival.core.contentPack.ContentPack;

public class ContentPackValidatorTest {

	private ContentPackFactory factory;

	@BeforeEach
	public void initialize() {
		factory = new ContentPackFactory();
	}

	@Test
	void minimalPackTest() {
		ContentPack pack = factory.minimalContentPack();
		ContentPackValidator validator = new ContentPackValidator();
		Assertions.assertEquals(0, validator.validate(pack).asList().size());
	}

	@Test
	void elementReferenceTest() {
		ContentPack pack = factory.minimalContentPack();
		ContentPackValidator validator = new ContentPackValidator();
		Assertions.assertEquals(0, validator.validate(pack).asList().size());
	}
}
