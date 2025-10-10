package com.mycompany.sample.frontend.components.tables.graves;

import java.time.LocalDate;
import java.util.List;

import com.mycompany.sample.backend.enums.ColumnType;
import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.frontend.components.tables.tableUtil.ColumnMeta;

public class GravesColumnDefinitions {
        private static final String PAST_DATE = "PAST_DATE";

        private static List<String> validators(String... names) {
                return List.of(names);
        }

        public static String getErrorMessage(List<String> validators, Object value) {
                for (String validator : validators) {
                        if (validator.equals(PAST_DATE) && (value instanceof LocalDate date)
                                        && date.isAfter(LocalDate.now())) {
                                return "Data nie może być w przyszłości";
                        }
                }
                return null;
        }
    public static final List<ColumnMeta<KsiegaGrobow>> COLUMNS_KSIEGA_GROBOW = List.of(
            new ColumnMeta<>("Kod grobu", ColumnType.READ,
                    KsiegaGrobow::getGraveIdCode,
                    null,
                    null),

            new ColumnMeta<>("Cmentarz", ColumnType.READ,
                    KsiegaGrobow::getCmentarz,
                    null,
                    null),

            new ColumnMeta<>("Rejon", ColumnType.READ,
                    KsiegaGrobow::getRejon,
                    null,
                    null),

            new ColumnMeta<>("Kwatera", ColumnType.READ,
                    KsiegaGrobow::getKwatera,
                    null,
                    null),

            new ColumnMeta<>("Rząd", ColumnType.READ,
                    KsiegaGrobow::getRzad,
                    null,
                    null),

            new ColumnMeta<>("Numer miejsca", ColumnType.READ,
                    KsiegaGrobow::getNumerMiejsca,
                    null,
                    null),

            new ColumnMeta<>("Ilość pochowanych", ColumnType.READ,
                    KsiegaGrobow::getIloscPochowanych,
                    null,
                    null),
            new ColumnMeta<>("Data ważności", ColumnType.READ,
                    KsiegaGrobow::getDataWaznosci,
                    null,
                    null),
            new ColumnMeta<>("Rodzaj grobu", ColumnType.GRAVETYPE,
                    KsiegaGrobow::getRodzajGrobu,
                    KsiegaGrobow::setRodzajGrobu,
                    null),
            new ColumnMeta<>("Wpis do rejestru zabytków", ColumnType.DATE,
                    KsiegaGrobow::getRejestrZabytkow,
                    KsiegaGrobow::setRejestrZabytkow,
                    validators(PAST_DATE)),
            new ColumnMeta<>("Lokalizacja ZDIZ", ColumnType.READ,
                    KsiegaGrobow::getLokalizacjaZDIZ,
                    null,
                    null));

}
