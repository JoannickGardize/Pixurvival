package com.pixurvival.contentPackEditor.util;

import org.junit.Assert;
import org.junit.Test;

import com.pixurvival.core.util.ReleaseVersion;

public class AutoUpgradeToolTest {

	@Test
	public void findReleaseVersionTest() {
		StringBuilder sb = new StringBuilder();
		Assert.assertNull(AutoUpgradeTool.findReleaseVersion(sb));
		sb = new StringBuilder("one\ntwo\nreleaseVersion: ALPHA_5");
		Assert.assertEquals(ReleaseVersion.ALPHA_5, AutoUpgradeTool.findReleaseVersion(sb));
		sb = new StringBuilder("one\ntwo\nreleaseVersion: ALPHA_5\nthree");
		Assert.assertEquals(ReleaseVersion.ALPHA_5, AutoUpgradeTool.findReleaseVersion(sb));
		sb = new StringBuilder("one\ntwo\nreleaseVersion: ABCDE");
		Assert.assertNull(AutoUpgradeTool.findReleaseVersion(sb));
	}
}
