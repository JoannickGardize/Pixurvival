package com.pixurvival.core.util;

/**
 * Enum representing All the history of the release versions since the alpha 5.
 * The latest enum constant is current version. The ordinal of the constants is
 * the order of the release dates.
 * 
 * @author SharkHendrix
 *
 */
public enum ReleaseVersion {

	ALPHA_4,
	ALPHA_4B,
	ALPHA_5,
	ALPHA_5B,
	ALPHA_6,
	ALPHA_6B;

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

	public static ReleaseVersion actual() {
		return ReleaseVersion.values()[ReleaseVersion.values().length - 1];
	}
}
