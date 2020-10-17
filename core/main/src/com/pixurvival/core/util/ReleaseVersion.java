package com.pixurvival.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import lombok.Getter;

/**
 * Enum representing All the history of the release versions since the alpha 5.
 * The version.txt file contains the current version name, in lower case and
 * with '-' instead of '_'.
 * 
 * The ordinal of the constants is the order of the release dates.
 * 
 * @author SharkHendrix
 *
 */
public enum ReleaseVersion {

	ALPHA_4,
	ALPHA_4B,
	ALPHA_5;

	/**
	 * @return the actual version of the game
	 */
	private static @Getter ReleaseVersion actual;

	static {
		InputStream input = ReleaseVersion.class.getClassLoader().getResourceAsStream("version.txt");

		final int bufferSize = 1024;
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(input, StandardCharsets.UTF_8);
		int charsRead;
		try {
			while ((charsRead = in.read(buffer, 0, buffer.length)) > 0) {
				out.append(buffer, 0, charsRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		actual = ReleaseVersion.valueOf(out.toString().replace('-', '_').toUpperCase());
	}

	public String displayName() {
		if (name().indexOf('_') != -1) {
			String[] split = name().split("_");
			return CaseUtils.upperToCamelCase(split[0]) + " " + split[1];
		} else {
			return name();
		}
	}

	/**
	 * Same as {@link #valueOf(String)}, but returns null if no match instead of
	 * throwing exception.
	 * 
	 * @param s
	 * @return
	 */
	public static ReleaseVersion valueFor(String s) {
		if (s == null) {
			return null;
		}
		for (ReleaseVersion value : values()) {
			if (value.name().equals(s)) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Null safe version of {{@link #displayName()}
	 * 
	 * @param version
	 * @return
	 */
	public static String displayNameOf(ReleaseVersion version) {
		return version == null ? "Unknown" : version.displayName();
	}
}
