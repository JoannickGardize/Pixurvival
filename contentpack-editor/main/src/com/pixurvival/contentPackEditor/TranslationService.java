package com.pixurvival.contentPackEditor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.pixurvival.core.util.CaseUtils;

import lombok.Getter;

public class TranslationService {
	private static @Getter TranslationService instance = new TranslationService();

	private ResourceBundle bundle = ResourceBundle.getBundle("translation/translation");

	private TranslationService() {

	}

	public boolean containsKey(String key) {
		return bundle.containsKey(key);
	}

	public String getString(String key) {
		try {
			String result = bundle.getString(key);
			return result;
		} catch (MissingResourceException e) {
			return "??" + key + "??";
		}
	}

	public String getString(Enum<?> enumValue) {
		return getString(CaseUtils.pascalToCamelCase(enumValue.getClass().getSimpleName()) + "." + CaseUtils.upperToCamelCase(enumValue.toString()));
	}
}
