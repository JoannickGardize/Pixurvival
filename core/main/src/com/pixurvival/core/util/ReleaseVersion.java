package com.pixurvival.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import lombok.Getter;

public class ReleaseVersion {
	private static @Getter String value = "";

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
		value = out.toString();
	}
}
