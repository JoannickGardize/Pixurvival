package com.pixurvival.contentPackEditor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.Getter;

public class TranslationService {
	private static @Getter TranslationService instance = new TranslationService();

	private ResourceBundle bundle = ResourceBundle.getBundle("translation/translation");

	private TranslationService() {

	}

	public String getString(String key) {
		try {
			String result = bundle.getString(key);
			return result;
		} catch (MissingResourceException e) {
			return "??" + key + "??";
		}
	}
}
