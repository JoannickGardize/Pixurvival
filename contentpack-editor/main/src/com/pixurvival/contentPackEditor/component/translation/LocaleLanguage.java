package com.pixurvival.contentPackEditor.component.translation;

import com.pixurvival.contentPackEditor.TranslationService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Arrays;
import java.util.Locale;

@Value
@EqualsAndHashCode(of = "code")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocaleLanguage implements Comparable<LocaleLanguage> {

    public static final LocaleLanguage undefined = new LocaleLanguage("undefined", TranslationService.getInstance().getString("generic.undefined"));

    private String code;
    private String displayName;

    public LocaleLanguage(String code) {
        this.code = code;
        displayName = new Locale(code).getDisplayLanguage(Locale.US);
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public int compareTo(LocaleLanguage other) {
        return displayName.compareToIgnoreCase(other.displayName);
    }

    public static LocaleLanguage[] getLanguages() {
        String[] codes = Locale.getISOLanguages();
        LocaleLanguage[] result = new LocaleLanguage[codes.length];
        for (int i = 0; i < codes.length; i++) {
            result[i] = new LocaleLanguage(codes[i]);
        }
        Arrays.sort(result);
        LocaleLanguage[] finalResult = new LocaleLanguage[codes.length + 1];
        System.arraycopy(result, 0, finalResult, 1, codes.length);
        return finalResult;
    }
}
