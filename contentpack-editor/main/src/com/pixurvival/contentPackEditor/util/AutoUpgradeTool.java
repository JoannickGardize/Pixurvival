package com.pixurvival.contentPackEditor.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.zip.ZipFile;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.component.tree.LayoutManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.serialization.ContentPackSerialization;
import com.pixurvival.core.util.FileUtils;
import com.pixurvival.core.util.ReleaseVersion;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AutoUpgradeTool {

	private static final String RELEASE_VERSION_YML_ATTRIBUTE = "releaseVersion: ";

	@Getter
	@Builder
	private static class Upgrader {
		@Default
		private Consumer<StringBuilder> coreUpgrader = sb -> {
			// Empty by default
		};
		@Default
		private Consumer<StringBuilder> layoutUpgrader = sb -> {
			// Empty by default
		};
	}

	private static final Map<Integer, Consumer<StringBuilder>> SERIALIZATION_UPGRADERS = new HashMap<>();

	private static final Map<Integer, Consumer<StringBuilder>> LAYOUT_UPGRADERS = new HashMap<>();

	static {
		SERIALIZATION_UPGRADERS.put(ReleaseVersion.ALPHA_5.ordinal(), sb -> {
			replaceAll(sb, "mapGenerators:", "mapProviders:");
			replaceAll(sb, "mapGenerator:", "mapProvider:");
		});
		LAYOUT_UPGRADERS.put(ReleaseVersion.ALPHA_5.ordinal(), sb -> {
			replaceAll(sb, "MAP_GENERATOR", "MAP_PROVIDER");
		});
	}

	/**
	 * @return null if the upgrade failed
	 */
	public static ContentPack upgrade() {
		FileService fileService = FileService.getInstance();
		try (ZipFile zipFile = new ZipFile(fileService.getCurrentFile())) {
			StringBuilder sb = asStringBuilder(zipFile, ContentPackSerialization.SERIALIZATION_ENTRY_NAME);
			int startIndex = startIndexFor(findReleaseVersion(sb));
			upgradeEntry(sb, SERIALIZATION_UPGRADERS, startIndex);
			ContentPack contentPack = fileService.getContentPackContext().getSerialization().reloadCore(fileService.getCurrentContentPack(), new ByteArrayInputStream(sb.toString().getBytes()));
			sb = asStringBuilder(zipFile, LayoutManager.LAYOUT_ENTRY);
			upgradeEntry(sb, LAYOUT_UPGRADERS, startIndex);
			LayoutManager.getInstance().read(new ByteArrayInputStream(sb.toString().getBytes()));
			LayoutManager.getInstance().refresh(contentPack);
			return contentPack;
		} catch (IOException e) {
			DialogUtils.showErrorDialog("autoUpgradeTool.error", e);
			return null;
		}
	}

	private static StringBuilder asStringBuilder(ZipFile zipFile, String entryName) throws IOException {
		byte[] bytes = FileUtils.readBytes(zipFile.getInputStream(zipFile.getEntry(entryName)));
		return new StringBuilder(new String(bytes, StandardCharsets.UTF_8));
	}

	private static void upgradeEntry(StringBuilder sb, Map<Integer, Consumer<StringBuilder>> upgrader, int startIndex) {
		for (int i = startIndex; i < ReleaseVersion.values().length; i++) {
			upgrader.get(i).accept(sb);
		}
	}

	private static int startIndexFor(ReleaseVersion version) {
		return version == null ? 0 : version.ordinal() + 1;
	}

	private static void replaceAll(StringBuilder sb, String find, String replace) {
		int index = sb.indexOf(find);
		while (index != -1) {
			sb.replace(index, find.length(), replace);
			index = sb.indexOf(find, index + replace.length());
		}
	}

	static ReleaseVersion findReleaseVersion(StringBuilder sb) {
		int index = sb.indexOf(RELEASE_VERSION_YML_ATTRIBUTE);
		int beginIndex = index + RELEASE_VERSION_YML_ATTRIBUTE.length();
		if (index == -1 && sb.length() < beginIndex) {
			return null;
		} else {
			int endIndex = sb.indexOf("\n", beginIndex);
			if (endIndex == -1) {
				endIndex = sb.length();
			}
			return ReleaseVersion.valueFor(sb.substring(beginIndex, endIndex));
		}
	}
}
