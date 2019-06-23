package com.pixurvival.core.contentPack;

import java.io.Serializable;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Version implements Comparable<Version>, Serializable {

	private static final long serialVersionUID = 1L;

	@Bounds(min = 0)
	private int major;

	@Bounds(min = 0)
	private int minor;

	public Version(String s) {
		String[] split = s.split("\\.");
		if (split.length != 2) {
			throw new IllegalArgumentException("Illegal version string pattern.");
		}
		major = Integer.parseInt(split[0]);
		if (major < 0) {
			throw new IllegalArgumentException("Major version cannot be negative.");
		}
		minor = Integer.parseInt(split[1]);
		if (minor < 0) {
			throw new IllegalArgumentException("Minor version cannot be negative.");
		}
	}

	@Override
	public int compareTo(Version o) {
		if (major == o.major) {
			return minor - o.minor;
		} else {
			return major - o.major;
		}
	}

	@Override
	public String toString() {
		return major + "." + minor;
	}
}
