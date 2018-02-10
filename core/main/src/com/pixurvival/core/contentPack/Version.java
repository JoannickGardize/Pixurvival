package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@XmlJavaTypeAdapter(Version.Adapter.class)
public class Version implements Comparable<Version> {

	private int major;
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

	public static class Adapter extends XmlAdapter<String, Version> {

		@Override
		public Version unmarshal(String v) throws Exception {
			try {
				return new Version(v);
			} catch (Exception e) {
				throw new ContentPackReadException(e);
			}
		}

		@Override
		public String marshal(Version v) throws Exception {
			return v.toString();
		}

	}
}
