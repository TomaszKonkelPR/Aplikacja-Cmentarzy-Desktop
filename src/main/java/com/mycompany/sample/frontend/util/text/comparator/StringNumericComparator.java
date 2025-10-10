package com.mycompany.sample.frontend.util.text.comparator;

import java.util.Comparator;

public class StringNumericComparator {
    public static final Comparator<String> STRING_WITH_NUMERIC_SUFFIX_COMPARATOR = (a, b) -> {
        if (a == null)
            a = "";
        if (b == null)
            b = "";

        java.util.regex.Matcher ma = java.util.regex.Pattern.compile("^(.*?)(\\d+)$").matcher(a);
        java.util.regex.Matcher mb = java.util.regex.Pattern.compile("^(.*?)(\\d+)$").matcher(b);

        boolean aHasNum = ma.matches();
        boolean bHasNum = mb.matches();

        if (aHasNum && bHasNum) {
            String prefixA = ma.group(1).trim();
            String prefixB = mb.group(1).trim();
            int cmp = prefixA.compareToIgnoreCase(prefixB);
            if (cmp != 0)
                return cmp;

            int numA = Integer.parseInt(ma.group(2));
            int numB = Integer.parseInt(mb.group(2));
            return Integer.compare(numA, numB);
        }

        return a.compareToIgnoreCase(b);
    };

    public static final Comparator<String> NUMERIC_WITH_SUFFIX_COMPARATOR = (a, b) -> {
        if (a == null)
            a = "";
        if (b == null)
            b = "";

        java.util.regex.Matcher ma = java.util.regex.Pattern.compile("^(\\d+)(.*)$").matcher(a);
        java.util.regex.Matcher mb = java.util.regex.Pattern.compile("^(\\d+)(.*)$").matcher(b);

        boolean aIsNum = ma.matches();
        boolean bIsNum = mb.matches();

        if (aIsNum && !bIsNum)
            return -1;
        if (!aIsNum && bIsNum)
            return 1;

        if (aIsNum && bIsNum) {
            int na = Integer.parseInt(ma.group(1));
            int nb = Integer.parseInt(mb.group(1));
            if (na != nb)
                return Integer.compare(na, nb);

            String sa = ma.group(2);
            String sb = mb.group(2);
            int suf = sa.compareToIgnoreCase(sb);
            if (suf != 0)
                return suf;
        }

        return a.compareToIgnoreCase(b);
    };
}
