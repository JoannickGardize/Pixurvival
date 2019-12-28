package com.pixurvival.core.contentPack;

import org.junit.Assert;
import org.junit.Test;

public class ContentPackIdentifierTest {

	@Test
	public void matchesContentPackFileNameTest() {
		Assert.assertTrue(ContentPackIdentifier.matchesContentPackFileName("AlekhAZE-56_1.0.zip"));
		Assert.assertTrue(ContentPackIdentifier.matchesContentPackFileName("57---zrf-AZE_500.001.zip"));
		Assert.assertFalse(ContentPackIdentifier.matchesContentPackFileName("_1.1.zip"));
		Assert.assertFalse(ContentPackIdentifier.matchesContentPackFileName("kj_1.zip"));
		Assert.assertFalse(ContentPackIdentifier.matchesContentPackFileName("kjk__1.0.zip"));
		Assert.assertFalse(ContentPackIdentifier.matchesContentPackFileName("AZE%d_2.0.zip"));
		Assert.assertFalse(ContentPackIdentifier.matchesContentPackFileName("ABC_1.0.1.zip"));
		Assert.assertFalse(ContentPackIdentifier.matchesContentPackFileName("qsd_1.0.png"));
		Assert.assertFalse(ContentPackIdentifier.matchesContentPackFileName("a_1.0zip"));
		Assert.assertFalse(ContentPackIdentifier.matchesContentPackFileName("truc.zip"));
	}

	@Test
	public void getIndentifierIfValidTest() {
		Assert.assertNull(ContentPackIdentifier.getIndentifierIfValid("AlekhAZE-561.0.zip"));
		Assert.assertNull(ContentPackIdentifier.getIndentifierIfValid("AlekhAZE-56_01.0.zip"));
		Assert.assertNotNull(ContentPackIdentifier.getIndentifierIfValid("AlekhAZE-56_1.0.zip"));
	}
}
