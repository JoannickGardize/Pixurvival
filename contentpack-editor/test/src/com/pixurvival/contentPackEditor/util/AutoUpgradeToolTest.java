package com.pixurvival.contentPackEditor.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.pixurvival.core.util.ReleaseVersion;

public class AutoUpgradeToolTest {

	@Test
	public void findReleaseVersionTest() {
		StringBuilder sb = new StringBuilder();
		Assertions.assertNull(AutoUpgradeTool.findReleaseVersion(sb));
		sb = new StringBuilder("one\ntwo\nreleaseVersion: ALPHA_5");
		Assertions.assertEquals(ReleaseVersion.ALPHA_5, AutoUpgradeTool.findReleaseVersion(sb));
		sb = new StringBuilder("one\ntwo\nreleaseVersion: ALPHA_5\nthree");
		Assertions.assertEquals(ReleaseVersion.ALPHA_5, AutoUpgradeTool.findReleaseVersion(sb));
		sb = new StringBuilder("one\ntwo\nreleaseVersion: ABCDE");
		Assertions.assertEquals(ReleaseVersion.OLDER, AutoUpgradeTool.findReleaseVersion(sb));
	}
}
