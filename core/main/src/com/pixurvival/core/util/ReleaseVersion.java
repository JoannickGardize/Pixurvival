package com.pixurvival.core.util;

import java.util.Objects;
import java.util.function.Predicate;

import lombok.AllArgsConstructor;

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

	OLDER(BackwardCompatibility.NONE),
	ALPHA_4(BackwardCompatibility.NONE),
	ALPHA_4B(BackwardCompatibility.NONE),
	ALPHA_5(BackwardCompatibility.NONE),
	ALPHA_5B(BackwardCompatibility.NONE),
	ALPHA_6(BackwardCompatibility.NONE),
	ALPHA_6B(BackwardCompatibility.FULL),
	ALPHA_7(BackwardCompatibility.FULL),
	ALPHA_8(BackwardCompatibility.CONTENT_PACK_ONLY),
	ALPHA_8B(BackwardCompatibility.FULL),
	ALPHA_9(BackwardCompatibility.NONE),
	ALPHA_10(BackwardCompatibility.NONE),
	ALPHA_11(BackwardCompatibility.FULL),
	ALPHA_12(BackwardCompatibility.NONE),
	ALPHA_12B(BackwardCompatibility.FULL),
	ALPHA_12C(BackwardCompatibility.FULL),
	ALPHA_13(BackwardCompatibility.NONE);

	private BackwardCompatibility backwardCompatibility;

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

	public static String displayNameOf(String constantName) {
		ReleaseVersion version = ReleaseVersion.valueFor(constantName);
		return version == null ? "Unknown" : version.displayName();
	}

	/**
	 * @return The actual release version, which is always the latest constant of
	 *         this enum.
	 */
	public static ReleaseVersion actual() {
		return ReleaseVersion.values()[ReleaseVersion.values().length - 1];
	}

	public boolean isContentPackCompatibleWith(ReleaseVersion other) {
		return isCompatibleWith(other, r -> r.backwardCompatibility.isContentPacks());
	}

	public boolean isSavesCompatibleWith(ReleaseVersion other) {
		return isCompatibleWith(other, r -> r.backwardCompatibility.isSaves());
	}

	/**
	 * <p>
	 * Check if this release version is compatible with the one in parameter,
	 * according to the result of {@code flagGetter} of each intermediate versions.
	 * <p>
	 * For two release versions, {@code v1.isCompatibleWith(v2)} will always returns
	 * the same result as {@code v2.isCompatibleWith(v1)}.
	 *
	 * @param other      the version to check if this version is compatible with.
	 * @param flagGetter
	 *
	 * @return true if the versions are compatible, false otherwise.
	 */
	private boolean isCompatibleWith(ReleaseVersion other, Predicate<ReleaseVersion> flagGetter) {
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
			if (!flagGetter.test(values()[i])) {
				return false;
			}
		}
		return true;
	}
}
