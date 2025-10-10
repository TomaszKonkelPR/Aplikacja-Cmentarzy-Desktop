package com.mycompany.sample.backend.generator;

public class GenerateGraveFromCode {

    public static String partFromCode(String code, int idx) {
        String[] p = parseCodeToParts(code);
        return (p == null || p[idx] == null || p[idx].isBlank()) ? "-" : p[idx];
    }

    private static String[] parseCodeToParts(String code) {
        if (code == null)
            return null;

        code = code.trim().replaceAll("\\s+", " ");
        if (code.isEmpty())
            return null;

        String[] raw = code.split("/");
        for (int i = 0; i < raw.length; i++)
            raw[i] = raw[i].trim();

        if (raw.length == 5) {
            return new String[] { raw[0], emptyToNull(raw[1]), raw[2], raw[3], raw[4] };
        }
        if (raw.length == 4) {
            String cem = raw[0];
            String second = raw[1];
            String rzad = raw[2];
            String miejsce = raw[3];

            int sp = second.indexOf(' ');
            if (sp > 0) {
                String kw = second.substring(0, sp).trim();
                String rejon = second.substring(sp + 1).trim();
                return new String[] { cem, emptyToNull(rejon), kw, rzad, miejsce };
            } else {
                return new String[] { cem, null, second, rzad, miejsce };
            }
        }
        return null;
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

}
