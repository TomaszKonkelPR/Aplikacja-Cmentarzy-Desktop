package com.mycompany.sample.frontend.components.tables.deceased.Add;

import java.time.LocalDate;
import java.util.List;

import com.mycompany.sample.backend.enums.ColumnType;
import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.components.tables.tableUtil.ColumnMeta;

public class DeceasedAddColumnDefinitions {
        private static final String NOT_EMPTY = "NOT_EMPTY";
        private static final String ONLY_LETTERS = "ONLY_LETTERS";
        private static final String PAST_DATE = "PAST_DATE";
        private static final String ANY_VALUE = "ANY_VALUE";

        private static List<String> validators(String... names) {
                return List.of(names);
        }

        public static String getErrorMessage(List<String> validators, Object value) {
                for (String validator : validators) {
                        if (validator.equals(NOT_EMPTY) && (value == null || value.toString().isBlank())) {
                                return "Pole nie może być puste";
                        }
                        if (validator.equals(ONLY_LETTERS)) {
                                if (value == null || value.toString().isBlank()) {
                                        return null;
                                }
                                if (!value.toString().matches("[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\s-]+")) {
                                        return "Dozwolone są tylko litery";
                                }
                        }
                        if (validator.equals(PAST_DATE) && (value instanceof LocalDate date)
                                        && date.isAfter(LocalDate.now())) {
                                return "Data nie może być w przyszłości";
                        }
                }
                return null;
        }

        public static final List<ColumnMeta<KsiegaZmarlych>> COLUMNS_KSIEGA_ZMARLYCH = List.of(
                        new ColumnMeta<>("Numer Ewidencyjny", ColumnType.STRING, KsiegaZmarlych::getNumerEwidencyjny,
                                        KsiegaZmarlych::setNumerEwidencyjny, validators(ANY_VALUE)),
                        new ColumnMeta<>("Imię", ColumnType.STRING, KsiegaZmarlych::getImie, KsiegaZmarlych::setImie,
                                        validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Nazwisko", ColumnType.STRING, KsiegaZmarlych::getNazwisko,
                                        KsiegaZmarlych::setNazwisko,
                                        validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Data Pochowania", ColumnType.DATE, KsiegaZmarlych::getDataPochowania,
                                        KsiegaZmarlych::setDataPochowania, validators(NOT_EMPTY, PAST_DATE)),
                        new ColumnMeta<>("Pochowany w", ColumnType.STRING,
                                        KsiegaZmarlych::getPochowanyW, KsiegaZmarlych::setPochowanyW,
                                        validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Uwagi", ColumnType.STRING, KsiegaZmarlych::getUwagi, KsiegaZmarlych::setUwagi,
                                        validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Uwagi, zastrzegający : imię i nazwisko, adres, dowód osobisty, seria numer",
                                        ColumnType.STRING, KsiegaZmarlych::getAdnotacja, KsiegaZmarlych::setAdnotacja,
                                        validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Nazwisko Rodowe", ColumnType.STRING, KsiegaZmarlych::getNazwiskoRodowe,
                                        KsiegaZmarlych::setNazwiskoRodowe, validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Stan Cywilny", ColumnType.STRING, KsiegaZmarlych::getStanCywilny,
                                        KsiegaZmarlych::setStanCywilny, validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Data Urodzenia", ColumnType.DATE, KsiegaZmarlych::getDataUrodzenia,
                                        KsiegaZmarlych::setDataUrodzenia, validators(PAST_DATE)),
                        new ColumnMeta<>("Miejsce Urodzenia", ColumnType.STRING, KsiegaZmarlych::getMiejsceUrodzenia,
                                        KsiegaZmarlych::setMiejsceUrodzenia, validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Data Zgonu", ColumnType.DATE, KsiegaZmarlych::getDataZgonu,
                                        KsiegaZmarlych::setDataZgonu,
                                        validators(PAST_DATE)),
                        new ColumnMeta<>("Miejsce Zgonu", ColumnType.STRING, KsiegaZmarlych::getMiejsceZgonu,
                                        KsiegaZmarlych::setMiejsceZgonu, validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Ostatnie Miejsce Zamieszkania", ColumnType.STRING,
                                        KsiegaZmarlych::getOstatnieMiejsceZamieszkania,
                                        KsiegaZmarlych::setOstatnieMiejsceZamieszkania,
                                        validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Przyczyna zgonu", ColumnType.STRING,
                                        KsiegaZmarlych::getPrzyczynaZgonu, KsiegaZmarlych::setPrzyczynaZgonu,
                                        validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Dziecko Martwo Narodzone", ColumnType.BOOLEAN,
                                        KsiegaZmarlych::isDzieckoMartwoNarodzone,
                                        KsiegaZmarlych::setDzieckoMartwoNarodzone, validators(ANY_VALUE)),
                        new ColumnMeta<>("Choroba Zakaźna", ColumnType.BOOLEAN, KsiegaZmarlych::isChorobaZakazna,
                                        KsiegaZmarlych::setChorobaZakazna, validators(ANY_VALUE)),
                        new ColumnMeta<>("Imię Ojca", ColumnType.STRING, KsiegaZmarlych::getImieOjca,
                                        KsiegaZmarlych::setImieOjca,
                                        validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Imię Matki", ColumnType.STRING, KsiegaZmarlych::getImieMatki,
                                        KsiegaZmarlych::setImieMatki, validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Nazwisko Ojca", ColumnType.STRING, KsiegaZmarlych::getNazwiskoOjca,
                                        KsiegaZmarlych::setNazwiskoOjca, validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Nazwisko Matki", ColumnType.STRING, KsiegaZmarlych::getNazwiskoMatki,
                                        KsiegaZmarlych::setNazwiskoMatki, validators(ONLY_LETTERS)),

                        new ColumnMeta<>("Data Ekshumacji", ColumnType.DATE, KsiegaZmarlych::getDataEkshumacji,
                                        KsiegaZmarlych::setDataEkshumacji, validators(PAST_DATE)),
                        new ColumnMeta<>("Miejsce przed ekshumacją", ColumnType.STRING,
                                        KsiegaZmarlych::getMiejscePrzedEkshumacja,
                                        KsiegaZmarlych::setMiejscePrzedEkshumacja,
                                        validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Data Ponownego Pochówku", ColumnType.DATE,
                                        KsiegaZmarlych::getDataPonownegoPochowku,
                                        KsiegaZmarlych::setDataPonownegoPochowku, validators(PAST_DATE)),
                        new ColumnMeta<>("Miejsce Ponownego Pochówku", ColumnType.STRING,
                                        KsiegaZmarlych::getMiejscePonownegoPochowku,
                                        KsiegaZmarlych::setMiejscePonownegoPochowku,
                                        validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Adres nowego cmentarza", ColumnType.STRING,
                                        KsiegaZmarlych::getAdresNowegoCmentarza,
                                        KsiegaZmarlych::setAdresNowegoCmentarza,
                                        validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Rodzaj grobu", ColumnType.STRING,
                                        KsiegaZmarlych::getRodzajGrobu, KsiegaZmarlych::setRodzajGrobu,
                                        validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Numer Aktu USC", ColumnType.STRING, KsiegaZmarlych::getNumerAktuUSC,
                                        KsiegaZmarlych::setNumerAktuUSC, validators(ANY_VALUE)),
                        new ColumnMeta<>("Data USC", ColumnType.DATE, KsiegaZmarlych::getDataUSC,
                                        KsiegaZmarlych::setDataUSC,
                                        validators(PAST_DATE)),
                        new ColumnMeta<>("Nazwa USC", ColumnType.STRING, KsiegaZmarlych::getNazwaUSC,
                                        KsiegaZmarlych::setNazwaUSC,
                                        validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Imię Dysponenta", ColumnType.STRING, KsiegaZmarlych::getImieDysponenta,
                                        KsiegaZmarlych::setImieDysponenta, validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Nazwisko Dysponenta", ColumnType.STRING,
                                        KsiegaZmarlych::getNazwiskoDysponenta,
                                        KsiegaZmarlych::setNazwiskoDysponenta, validators(ONLY_LETTERS)),
                        new ColumnMeta<>("Organ", ColumnType.STRING, KsiegaZmarlych::getOrgan, KsiegaZmarlych::setOrgan,
                                        validators(ANY_VALUE)),
                        new ColumnMeta<>("Miejsce pochowania", ColumnType.STRING,
                                        KsiegaZmarlych::getMiejscePochowania, KsiegaZmarlych::setMiejscePochowania,
                                        validators(ONLY_LETTERS)));

}
