package com.mycompany.sample.frontend.util.text.normalizer;

public class NormalizerText {
    public static String normalizeGraveInput(String value) {
        if (value == null)
            return null;
        value = value.toUpperCase();
        value = value.replaceAll("(\\d)\\s+([A-Z])(?=\\s|$)", "$1$2");
        value = value.replace("/", "-");

        return value.trim();
    }
}
