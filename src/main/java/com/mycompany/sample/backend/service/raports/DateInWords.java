package com.mycompany.sample.backend.service.raports;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

public class DateInWords {
    private static final String[] MONTHS_NOM = {
        "styczeń","luty","marzec","kwiecień","maj","czerwiec",
        "lipiec","sierpień","wrzesień","październik","listopad","grudzień"
    };

    private static final String[] DAY_CARDINAL = {
        "", "jeden","dwa","trzy","cztery","pięć","sześć","siedem","osiem","dziewięć",
        "dziesięć","jedenaście","dwanaście","trzynaście","czternaście","piętnaście","szesnaście",
        "siedemnaście","osiemnaście","dziewiętnaście","dwadzieścia","dwadzieścia jeden","dwadzieścia dwa",
        "dwadzieścia trzy","dwadzieścia cztery","dwadzieścia pięć","dwadzieścia sześć","dwadzieścia siedem",
        "dwadzieścia osiem","dwadzieścia dziewięć","trzydzieści","trzydzieści jeden"
    };

    public static String toYMDWords(LocalDate date) {
        if (date == null) return "";
        String year = yearWords(date.getYear());
        String month = MONTHS_NOM[date.getMonthValue() - 1];
        String day = dayCardinal(date.getDayOfMonth());
        return (year + " " + month + " " + day).trim();
    }

    public static String toYMDWords(Date date) {
        return toYMDWords(toLocalDate(date));
    }


    private static LocalDate toLocalDate(Date d) {
        if (d == null) return null;
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static String dayCardinal(int d) {
        if (d >= 1 && d <= 31) return DAY_CARDINAL[d];
        return Integer.toString(d);
    }

    private static String yearWords(int y) {
        if (y == 2000) return "dwa tysiące";
        if (y > 2000 && y < 3000) return "dwa tysiące " + numberBelowThousand(y - 2000);
        if (y == 1900) return "tysiąc dziewięćset";
        if (y > 1900 && y < 2000) return "tysiąc dziewięćset " + numberBelowThousand(y - 1900);
        return Integer.toString(y);
    }

    private static String numberBelowThousand(int n) {
        String[] hundreds = {"","sto","dwieście","trzysta","czterysta","pięćset","sześćset","siedemset","osiemset","dziewięćset"};
        String[] tens     = {"","dziesięć","dwadzieścia","trzydzieści","czterdzieści","pięćdziesiąt","sześćdziesiąt","siedemdziesiąt","osiemdziesiąt","dziewięćdziesiąt"};
        String[] teens    = {"dziesięć","jedenaście","dwanaście","trzynaście","czternaście","piętnaście","szesnaście","siedemnaście","osiemnaście","dziewiętnaście"};
        String[] units    = {"","jeden","dwa","trzy","cztery","pięć","sześć","siedem","osiem","dziewięć"};

        if (n <= 0) return "";
        int h = n / 100, t = (n % 100) / 10, u = n % 10;

        StringBuilder sb = new StringBuilder();
        if (h > 0) sb.append(hundreds[h]).append(" ");
        if (t == 1) {
            sb.append(teens[u]);
        } else {
            if (t > 0) sb.append(tens[t]).append(" ");
            if (u > 0) sb.append(units[u]);
        }
        return sb.toString().trim();
    }
}

