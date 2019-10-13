package com.pixurvival.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LocaleUtils {
	private static List<Locale> allAvailableLocales = Arrays.asList(Locale.getAvailableLocales());

	public static Locale findBestMatch(Collection<Locale> requested, Collection<Locale> supported) {
		List<LanguageRange> languageRanges = toLanguageRanges(requested);
		List<Locale> allMatches = Locale.filter(languageRanges, allAvailableLocales);
		Locale bestMatch = Locale.lookup(toLanguageRanges(allMatches), supported);
		return bestMatch;
	}

	private static List<LanguageRange> toLanguageRanges(Collection<Locale> locales) {
		ArrayList<LanguageRange> languageRanges = new ArrayList<>();
		for (Locale locale : locales) {
			languageRanges.add(toLanguageRange(locale));
		}
		return languageRanges;
	}

	private static LanguageRange toLanguageRange(Locale locale) {
		return new LanguageRange(locale.toLanguageTag());
	}
}