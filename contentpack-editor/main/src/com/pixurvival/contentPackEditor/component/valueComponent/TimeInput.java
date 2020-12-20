package com.pixurvival.contentPackEditor.component.valueComponent;

public class TimeInput extends NumberInput<Long> {

	private static final long serialVersionUID = 1L;

	@Override
	protected Long parse(String text) {
		if (text.matches("\\d+(:\\d+(\\.\\d+)?)?")) {
			String[] split1 = text.split(":");
			long minutes = Long.parseLong(split1[0]);
			if (split1.length == 1) {
				// by default consider single number as seconds
				return minutes * 1000;
			}
			String[] split2 = split1[1].split("\\.");
			long seconds = Long.parseLong(split2[0]);
			if (split2.length == 1) {
				return minutes * 60 * 1000 + seconds * 1000;
			}
			long millis = Long.parseLong(split2[1]);
			return minutes * 60 * 1000 + seconds * 1000 + millis;
		} else {
			return null;
		}
	}

	@Override
	protected String format(Long value) {
		long millis = value % 1000;
		long seconds = (value / 1000) % 60;
		long minutes = value / 1000 / 60;
		return minutes + ":" + String.format("%02d", seconds) + "." + String.format("%03d", millis);
	}
}
