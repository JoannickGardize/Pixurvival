package com.pixurvival.contentPackEditor.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.zip.ZipFile;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.component.tree.LayoutManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.serialization.ContentPackSerialization;
import com.pixurvival.core.util.FileUtils;
import com.pixurvival.core.util.ReleaseVersion;

import lombok.experimental.UtilityClass;

// TODO use replace regex
@UtilityClass
public class AutoUpgradeTool {

	private static final String RELEASE_VERSION_YML_ATTRIBUTE = "releaseVersion: ";

	private static final Map<Integer, Consumer<StringBuilder>> SERIALIZATION_UPGRADERS = new HashMap<>();
	private static final Map<Integer, Consumer<StringBuilder>> LAYOUT_UPGRADERS = new HashMap<>();
	private static final Map<Integer, Consumer<ContentPack>> CONTENT_PACK_UPGRADERS = new HashMap<>();

	static {
		SERIALIZATION_UPGRADERS.put(ReleaseVersion.ALPHA_5.ordinal(), sb -> {
			replaceAll(sb, "mapGenerators:", "mapProviders:");
			replaceAll(sb, "mapGenerator:", "mapProvider:");
			replaceAll(sb, "!!RemainingTeamCondition", "!!RemainingTeamEndCondition");
			replaceAll(sb, "!!NoEndCondition", "!!RemainingTeamEndCondition");
			forEachElementLine(sb, "mapProviders", line -> line + " !!ProcedurallyGeneratedMapProvider");
		});
		LAYOUT_UPGRADERS.put(ReleaseVersion.ALPHA_5.ordinal(), sb -> replaceAll(sb, "MAP_GENERATOR", "MAP_PROVIDER"));

		SERIALIZATION_UPGRADERS.put(ReleaseVersion.ALPHA_9.ordinal(), sb -> replaceAll(sb, "!!DropItemsBehavior", "!!DoNothingBehavior"));

		CONTENT_PACK_UPGRADERS.put(ReleaseVersion.ALPHA_10.ordinal(), cp -> cp.getEffects().forEach(e -> e.getRepeatFollowingElements().setBase(e.getRepeatFollowingElements().getBase() + 1f)));

		SERIALIZATION_UPGRADERS.put(ReleaseVersion.ALPHA_12.ordinal(), sb -> replaceAll(sb, "!!DamageableStructure", "!!Structure"));
	}

	/**
	 * @return null if the upgrade failed
	 * @throws ContentPackException
	 */
	public static ContentPack upgrade() {
		FileService fileService = FileService.getInstance();
		try (ZipFile zipFile = new ZipFile(fileService.getCurrentFile())) {
			StringBuilder serializationSb = asStringBuilder(zipFile, ContentPackSerialization.SERIALIZATION_ENTRY_NAME);
			int startIndex = startIndexFor(findReleaseVersion(serializationSb));
			upgradeEntry(serializationSb, SERIALIZATION_UPGRADERS, startIndex);
			StringBuilder layoutSb = asStringBuilder(zipFile, LayoutManager.LAYOUT_ENTRY);
			upgradeEntry(layoutSb, LAYOUT_UPGRADERS, startIndex);
			LayoutManager.getInstance().setOverridedSource(new ByteArrayInputStream(layoutSb.toString().getBytes()));
			ContentPack contentPack = fileService.getContentPackContext().getSerialization().load(fileService.getCurrentFile(), new ByteArrayInputStream(serializationSb.toString().getBytes()));
			upgradeContentPack(contentPack, startIndex);
			LayoutManager.getInstance().setOverridedSource(null);
			return contentPack;
		} catch (IOException | ContentPackException e) {
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
			upgrader.getOrDefault(i, s -> {
			}).accept(sb);
		}
	}

	private static void upgradeContentPack(ContentPack contentPack, int startIndex) {
		for (int i = startIndex; i < ReleaseVersion.values().length; i++) {
			CONTENT_PACK_UPGRADERS.getOrDefault(i, s -> {
			}).accept(contentPack);
		}
	}

	private static int startIndexFor(ReleaseVersion version) {
		return version == null ? 0 : version.ordinal() + 1;
	}

	private static void replaceAll(StringBuilder sb, String find, String replace) {
		int index = sb.indexOf(find);
		while (index != -1) {
			sb.replace(index, index + find.length(), replace);
			index = sb.indexOf(find, index + replace.length());
		}
	}

	private static void forEachElementLine(StringBuilder sb, String attributeName, UnaryOperator<String> replacementOperator) {
		String startLine = "\n" + attributeName + ":\n";
		int currentIndex = sb.indexOf(startLine) + startLine.length();
		while (currentIndex < sb.length() - 1) {
			char firstChar = sb.charAt(currentIndex);
			if (firstChar == '-') {
				int nextLineIndex = sb.indexOf("\n", currentIndex);
				String replacement = replacementOperator.apply(sb.substring(currentIndex, nextLineIndex));
				sb.replace(currentIndex, nextLineIndex, replacement);
			} else if (firstChar != ' ') {
				break;
			}
			currentIndex += sb.indexOf("\n", currentIndex) + 1;
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
