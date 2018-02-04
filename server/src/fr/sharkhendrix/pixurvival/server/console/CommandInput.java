package fr.sharkhendrix.pixurvival.server.console;

import java.util.Arrays;

import lombok.Getter;

public class CommandInput {

	private @Getter String name;
	private String[] args;

	public CommandInput(String input) {
		String[] split = input.trim().toLowerCase().split("\\s");
		if (split.length == 0) {
			name = "";
			args = new String[0];
		} else {
			name = split[0];
			args = Arrays.copyOfRange(split, 1, split.length);
		}
	}
	
	

	public int argsLength() {
		return args.length;
	}

	public String getArg(int index) {
		return args[index];
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(name);
		if (argsLength() > 0) {
			for (String arg : args) {
				sb.append(" ").append(arg);
			}
		}
		return sb.toString();
	}
}
