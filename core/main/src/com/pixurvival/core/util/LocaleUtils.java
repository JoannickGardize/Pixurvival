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
		if (bestMatch == null) {
			return supported.iterator().next();
		} else {
			return bestMatch;
		}
	}

	public static Collection<Locale> toLocale(Collection<String> localeTags) {
		List<Locale> locales = new ArrayList<>();
		for (String localTag : localeTags) {
			locales.add(Locale.forLanguageTag(localTag));
		}
		return locales;
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
