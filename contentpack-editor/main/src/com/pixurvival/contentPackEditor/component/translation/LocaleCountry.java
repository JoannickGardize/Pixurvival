package com.pixurvival.contentPackEditor.component.translation;

import java.util.Arrays;
import java.util.Locale;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

import com.pixurvival.contentPackEditor.TranslationService;

@Value
@EqualsAndHashCode(of = "code")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocaleCountry implements Comparable<LocaleCountry> {

	public static final LocaleCountry undefined = new LocaleCountry("undefined", TranslationService.getInstance().getString("generic.undefined"));

	private String code;
	private String displayName;

	public LocaleCountry(String code) {
		this.code = code;
		displayName = new Locale("en", code).getDisplayCountry(Locale.US);
	}

	@Override
	public String toString() {
		return displayName;
	}

	@Override
	public int compareTo(LocaleCountry other) {
		return displayName.compareToIgnoreCase(other.displayName);
	}

	public static LocaleCountry[] getCountries() {
		String[] codes = Locale.getISOCountries();
		LocaleCountry[] result = new LocaleCountry[codes.length];
		for (int i = 0; i < codes.length; i++) {
			result[i] = new LocaleCountry(codes[i]);
		}
		Arrays.sort(result);
		LocaleCountry[] finalResult = new LocaleCountry[codes.length + 1];
		System.arraycopy(result, 0, finalResult, 1, codes.length);
		return finalResult;
	}
}
