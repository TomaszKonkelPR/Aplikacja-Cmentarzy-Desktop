package com.mycompany.sample.backend.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import com.mycompany.sample.backend.models.KsiegaZmarlych;

public class MapRowToZmarly {
    private static final List<String> DATE_PATTERNS = Arrays.asList(
            "yyyy-MM-dd",
            "dd-MM-yyyy",
            "yyyy/MM/dd",
            "dd/MM/yyyy",
            "dd.MM.yyyy",
            "yyyy.MM.dd",
            "EEE MMM dd HH:mm:ss z yyyy");

    public static KsiegaZmarlych map(Row row) {
        String rejonZDIZ = getCellValue(row.getCell(43));
        String kwateraZDIZ = getCellValue(row.getCell(44));
        String rzadZDIZ = getCellValue(row.getCell(45));
        String miejsceZDIZ = getCellValue(row.getCell(46));

        String lokalizacjaZDIZ = String.format(
                "Rejon - %s, Kwatera - %s, Rząd - %s, Miejsce - %s",
                rejonZDIZ, kwateraZDIZ, rzadZDIZ, miejsceZDIZ);
        return KsiegaZmarlych.builder()
                .numerEwidencyjny(getCellValue(row.getCell(3)))
                .dzieckoMartwoNarodzone(parseBoolean(getCellValue(row.getCell(4))))
                .imie(getCellValue(row.getCell(5)))
                .nazwisko(getCellValue(row.getCell(6)))
                .nazwiskoRodowe(getCellValue(row.getCell(7)))
                .stanCywilny(getCellValue(row.getCell(8)))
                .dataUrodzenia(parseCellToDate(row.getCell(9)))
                .miejsceUrodzenia(getCellValue(row.getCell(10)))
                .dataZgonu(parseCellToDate(row.getCell(11)))
                .miejsceZgonu(getCellValue(row.getCell(12)))
                .ostatnieMiejsceZamieszkania(getCellValue(row.getCell(13)))
                .chorobaZakazna(parseBoolean(getCellValue(row.getCell(14))))
                .przyczynaZgonu(getCellValue(row.getCell(15)))
                .imieOjca(getCellValue(row.getCell(16)))
                .imieMatki(getCellValue(row.getCell(17)))
                .nazwiskoOjca(getCellValue(row.getCell(18)))
                .nazwiskoMatki(getCellValue(row.getCell(19)))
                .dataPochowania(parseCellToDate(row.getCell(20)))
                .dataEkshumacji(parseCellToDate(row.getCell(21)))
                .miejscePrzedEkshumacja(getCellValue(row.getCell(22)))
                .dataPonownegoPochowku(parseCellToDate(row.getCell(24)))
                .miejscePonownegoPochowku(getCellValue(row.getCell(25)))
                .adresNowegoCmentarza(getCellValue(row.getCell(26)))
                .rodzajGrobu(getCellValue(row.getCell(27)))
                .numerAktuUSC(getCellValue(row.getCell(28)))
                .dataUSC(parseCellToDate(row.getCell(29)))
                .nazwaUSC(getCellValue(row.getCell(30)))
                .imieDysponenta(getCellValue(row.getCell(31)))
                .nazwiskoDysponenta(getCellValue(row.getCell(32)))
                .organ(getCellValue(row.getCell(33)))
                .pochowanyW(getCellValue(row.getCell(34)))
                .uwagi(getCellValue(row.getCell(35)))
                .miejscePochowania(getCellValue(row.getCell(36)))
                .lokalizacjaZDIZ(lokalizacjaZDIZ)
                .build();

    }

    public static String getCellValue(Cell cell) {
        if (cell == null)
            return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double val = cell.getNumericCellValue();
                    if (val == (long) val) {
                        return String.valueOf((long) val);
                    } else {
                        return String.valueOf(val);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return "[formuły nie są wspierane w StreamingReader]";
            case BLANK:
            case _NONE:
                return "";
            default:
                return "[Nieznany typ]";
        }
    }

    public static LocalDate parseCellToDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            CellType cellType = cell.getCellType();

            if (cellType == CellType.STRING) {
                String rawValue = cell.getStringCellValue().trim();

                if (!rawValue.isEmpty()) {

                    for (String pattern : DATE_PATTERNS) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
                            sdf.setLenient(false);
                            Date parsedDate = sdf.parse(rawValue);
                            return parsedDate.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                        } catch (ParseException ignored) {
                        }
                    }

                    try {
                        double numericValue = Double.parseDouble(rawValue);
                        if (DateUtil.isValidExcelDate(numericValue)) {
                            Date date = DateUtil.getJavaDate(numericValue);
                            return date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            if (cellType == CellType.NUMERIC) {
                double numericValue = cell.getNumericCellValue();
                if (DateUtil.isCellDateFormatted(cell) || DateUtil.isValidExcelDate(numericValue)) {
                    Date date = DateUtil.getJavaDate(numericValue);
                    return date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                }
            }

        } catch (Exception e) {
            System.err.println("Błąd parsowania daty z komórki: " + cell + " — " + e.getMessage());
        }

        return null;
    }

    public static boolean parseBoolean(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String v = value.trim().toLowerCase();
        return !(v.equals("nie") || v.equals("no") || v.equals("false") || v.equals("0"));
    }

}
