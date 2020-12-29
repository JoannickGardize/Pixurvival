package com.pixurvival.core.contentPack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pixurvival.core.contentPack.serialization.ContentPackSerialization;
import com.pixurvival.core.contentPack.serialization.ContentPackValidityCheckResult;
import com.pixurvival.core.contentPack.summary.ContentPackSummary;
import com.pixurvival.core.contentPack.validation.ContentPackValidator;
import com.pixurvival.core.contentPack.validation.ErrorNode;

import lombok.Getter;
import lombok.NonNull;

/**
 * Context of all installed content packs of the game, using a working
 * directory, which can be scanned to list available ContentPacks.
 * 
 * @author SharkHendrix
 *
 */
public class ContentPackContext {

	private @NonNull @Getter File workingDirectory;
	private List<ContentPackSummary> summaries;
	private @Getter ContentPackSerialization serialization;
	private ContentPackValidator validator;

	public ContentPackContext(File workingDirectory) {
		this.workingDirectory = workingDirectory;
		if (workingDirectory != null && !workingDirectory.isDirectory()) {
			throw new IllegalStateException("Not a directory : " + workingDirectory);
		}
		serialization = new ContentPackSerialization();
		validator = new ContentPackValidator();
	}

	public List<ContentPackSummary> list() {
		if (summaries != null) {
			return summaries;
		}
		refreshList();
		return summaries;
	}

	public void refreshList() {
		if (workingDirectory == null) {
			throw new IllegalStateException("No working directory defined");
		}
		summaries = new ArrayList<>();
		for (File file : workingDirectory.listFiles()) {
			if (!file.isFile()) {
				continue;
			}
			ContentPackSummary summary = serialization.readSummary(file);
			if (summary != null) {
				summaries.add(summary);
			}
		}

	}

	public File fileOf(ContentPackIdentifier identifier) throws ContentPackException {
		if (identifier.getFile() != null) {
			return identifier.getFile();
		} else {
			File file = findUnknownFileOf(identifier);
			if (file == null) {
				refreshList();
				file = findUnknownFileOf(identifier);
				if (file == null) {
					throw new ContentPackException("Could not find the file for " + identifier);
				}
			}
			return file;
		}
	}

	private File findUnknownFileOf(ContentPackIdentifier identifier) {
		for (ContentPackSummary summary : list()) {
			if (summary.getIdentifier().equals(identifier)) {
				File file = summary.getIdentifier().getFile();
				if (file != null) {
					identifier.setFile(file);
					return file;
				}
			}
		}
		return null;
	}

	public ContentPack load(ContentPackIdentifier identifier) throws ContentPackException {
		return serialization.load(fileOf(identifier));
	}

	public ContentPackValidityCheckResult checkSameness(ContentPackIdentifier identifier, byte[] checksum) throws ContentPackException {
		if (list().stream().noneMatch(s -> identifier.equals(s.getIdentifier()))) {
			return ContentPackValidityCheckResult.NOT_FOUND;
		} else if (!Arrays.equals(getChecksum(identifier), checksum)) {
			return ContentPackValidityCheckResult.NOT_SAME;
		} else {
			return ContentPackValidityCheckResult.OK;
		}
	}

	public List<ErrorNode> getErrors(ContentPack contentPack) {
		return validator.validate(contentPack).asList();
	}

	public byte[] getChecksum(ContentPackIdentifier identifier) throws ContentPackException {
		byte[] checksum = serialization.getChecksum(fileOf(identifier));
		identifier.setChecksum(checksum);
		return checksum;
	}

}
