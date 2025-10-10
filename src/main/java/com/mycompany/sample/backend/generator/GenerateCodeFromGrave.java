package com.mycompany.sample.backend.generator;

public class GenerateCodeFromGrave {
    public static String generateFullCode(String cemetery, String region, String quarter, String row, String place) {
        if (region == null || region.isBlank()) {
            return cemetery + "/" + quarter + "/" + row + "/" + place;
        } else if (region.toUpperCase().startsWith("KOL")) {
            return cemetery + "/" + region + "/" + quarter + "/" + row + "/" + place;
        } else {
            return cemetery + "/" + quarter + " " + region + "/" + row + "/" + place;
        }
    }

    public static String generatePartialCode(String cemeteryName, String region, String quarter) {
        if (region == null || region.isBlank()) {
            return cemeteryName + "/" + quarter + "/%";
        } else if (region.toUpperCase().startsWith("KOL")) {
            return cemeteryName + "/" + region + "/" + quarter + "/%";
        } else {
            return cemeteryName + "/" + quarter + " " + region + "/%";
        }
    }
}
