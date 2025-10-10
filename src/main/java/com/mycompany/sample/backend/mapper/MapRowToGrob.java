package com.mycompany.sample.backend.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import com.mycompany.sample.backend.enums.GraveAddType;
import com.mycompany.sample.backend.models.KsiegaGrobow;

public class MapRowToGrob {
    public static KsiegaGrobow map(Row row) {
        String rejonZDIZ = getCellValue(row.getCell(7));
        String kwateraZDIZ = getCellValue(row.getCell(8));
        String rzadZDIZ = getCellValue(row.getCell(9));
        String miejsceZDIZ = getCellValue(row.getCell(10));

        String lokalizacjaZDIZ = String.format(
                "Rejon - %s, Kwatera - %s, Rząd - %s, Miejsce - %s",
                rejonZDIZ, kwateraZDIZ, rzadZDIZ, miejsceZDIZ);

        return KsiegaGrobow.builder()
                .graveIdCode(getCellValue(row.getCell(1)))
                .cmentarz(getCellValue(row.getCell(2)))
                .rejon(getCellValue(row.getCell(3)))
                .kwatera(getCellValue(row.getCell(4)))
                .rzad(getCellValue(row.getCell(5)))
                .numerMiejsca(getCellValue(row.getCell(6)))
                .lokalizacjaZDIZ(lokalizacjaZDIZ)
                .addType(GraveAddType.IMPORTED)
                .build();
    }

    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double val = cell.getNumericCellValue();
                    return (val == (long) val) ? String.valueOf((long) val) : String.valueOf(val);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BLANK:
            case _NONE:
                return "";
            default:
                return "";
        }
    }

    public static LocalDate parseDate(String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            System.err.println("[WARN] Nie udało się sparsować daty: " + value);
            return null;
        }
    }
}
