package com.pixurvival.contentPackEditor.util;

import com.pixurvival.core.util.ReleaseVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AutoUpgradeToolTest {

    @Test
    void findReleaseVersionTest() {
        StringBuilder sb = new StringBuilder();
        Assertions.assertNull(AutoUpgradeTool.findReleaseVersion(sb));
        sb = new StringBuilder("one\ntwo\nreleaseVersion: ALPHA_5");
        Assertions.assertEquals(ReleaseVersion.ALPHA_5, AutoUpgradeTool.findReleaseVersion(sb));
        sb = new StringBuilder("one\ntwo\nreleaseVersion: ALPHA_5\nthree");
        Assertions.assertEquals(ReleaseVersion.ALPHA_5, AutoUpgradeTool.findReleaseVersion(sb));
        sb = new StringBuilder("one\ntwo\nreleaseVersion: ABCDE");
        Assertions.assertEquals(ReleaseVersion.OLDER, AutoUpgradeTool.findReleaseVersion(sb));
    }

    @Test
    void replaceNodeAfterEachNodeTest() {
        StringBuilder sb = new StringBuilder("Hello my name is Bob.\n Hello my name is Bob.\n Hey my name is Bob.");
        AutoUpgradeTool.replaceNodeAfterEachNode(sb, "Hello", "Bob", s -> "(" + s + ")" + "Jack");
        Assertions.assertEquals("Hello my name is (Hello my name is )Jack.\n Hello my name is ( Hello my name is )Jack.\n Hey my name is Bob.", sb.toString());
    }
}
