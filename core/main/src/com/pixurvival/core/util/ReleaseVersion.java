package com.pixurvival.core.util;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing All the history of the release versions since the alpha 5.
 * The latest enum constant is current version. The ordinal of the constants is
 * the order of the release dates.
 * 
 * @author SharkHendrix
 *
 */
@AllArgsConstructor
public enum ReleaseVersion {

	OLDER(false),
	ALPHA_4(false),
	ALPHA_4B(false),
	ALPHA_5(false),
	ALPHA_5B(false),
	ALPHA_6(false),
	ALPHA_6B(true),
	ALPHA_7(true);

	private @Getter boolean backwardCompatible;

	public String displayName() {
		if (name().indexOf('_') != -1) {
			String[] split = name().split("_");
			return CaseUtils.upperToCamelCase(split[0]) + " " + split[1];
		} else {
			return name();
		}
	}

	/**
	 * Same as {@link #valueOf(String)}, but returns {@link #OLDER} if no match
	 * instead of throwing exception.
	 * 
	 * @param s
	 * @return
	 */
	public static ReleaseVersion valueFor(String s) {
		if (s == null) {
			return OLDER;
		}
		for (ReleaseVersion value : values()) {
			if (value.name().equals(s)) {
				return value;
			}
		}
		return OLDER;
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

	/**
	 * @return The actual release version, which is always the latest constant of
	 *         this enum.
	 */
	public static ReleaseVersion actual() {
		return ReleaseVersion.values()[ReleaseVersion.values().length - 1];
	}

	/**
	 * <p>
	 * Check if this release version is compatible with the one in parameter for
	 * game saves and content packs, according to {@link #isBackwardCompatible()} of
	 * each intermediate versions.
	 * <p>
	 * For two release versions, {@code v1.isCompatibleWith(v2)} will always returns
	 * the same result as {@code v2.isCompatibleWith(v1)}.
	 * 
	 * @param other
	 *            the version to check if this version is compatible with.
	 * 
	 * @return true if the versions are compatible, false otherwise.
	 */
	public boolean isCompatibleWith(ReleaseVersion other) {
		Objects.requireNonNull(other);
		if (this == other) {
			return true;
		}
		ReleaseVersion oldest;
		ReleaseVersion newest;
		if (this.ordinal() > other.ordinal()) {
			oldest = other;
			newest = this;
		} else {
			oldest = this;
			newest = other;
		}
		for (int i = oldest.ordinal() + 1; i <= newest.ordinal(); i++) {
			if (!values()[i].isBackwardCompatible()) {
				return false;
			}
		}
		return true;
	}
}
