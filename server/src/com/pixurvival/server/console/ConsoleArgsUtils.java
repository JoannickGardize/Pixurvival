package com.pixurvival.server.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsoleArgsUtils {

	public static String[] splitArgs(String input) {
		List<String> result = new ArrayList<>();
		boolean escapingSpaces = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c == ' ' && !escapingSpaces) {
				if (sb.length() > 0) {
					result.add(sb.toString());
					sb.setLength(0);
				}
			} else if (c == '\"') {
				escapingSpaces = !escapingSpaces;
			} else {
				sb.append(c);
			}
		}
		if (sb.length() > 0) {
			result.add(sb.toString());
			sb.setLength(0);
		}
		return result.toArray(new String[result.size()]);
	}

	public static String[] subArgs(String[] args) {
		if (args.length == 0) {
			throw new IllegalArgumentException("Cannot create subargs of empty array");
		} else if (args.length == 1) {
			return new String[0];
		} else {
			return Arrays.copyOfRange(args, 1, args.length);
		}
	}
}
