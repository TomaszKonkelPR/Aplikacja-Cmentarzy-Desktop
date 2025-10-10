package com.mycompany.sample.frontend.components.tables.deceased.Edit;

import java.time.LocalDate;
import java.util.List;

import com.mycompany.sample.backend.enums.ColumnType;
import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.components.tables.tableUtil.ColumnMeta;

public class DeceasedEditColumnDefinitions {

        private static final String NOT_EMPTY = "NOT_EMPTY";
        private static final String ONLY_LETTERS = "ONLY_LETTERS";
        private static final String PAST_DATE = "PAST_DATE";
        private static final String ANY_VALUE = "ANY_VALUE";
        private static final String INVALID_FORMAT = "INVALID_FORMAT";

        private static List<String> validators(String... names) {
                return List.of(names);
        }

        public static String getErrorMessage(List<String> validators, Object value) {
                for (String validator : validators) {
                        if (validator.equals(INVALID_FORMAT) && value == null) {
                                return "Nieprawidłowy format. Poprawny: yyyy-MM-dd";
                        }
                        if (validator.equals(NOT_EMPTY) && (value == null || value.toString().isBlank())) {
                                return "Pole nie może być puste";
                        }
                        if (validator.equals(ONLY_LETTERS)
                                        && (value == null || !value.toString()
                                                        .matches("[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\s-]+"))) {
                                return "Dozwolone są tylko litery";
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
                                        KsiegaZmarlych::setNumerEwidencyjny, validators(NOT_EMPTY)),
                        new ColumnMeta<>("Imię", ColumnType.STRING, KsiegaZmarlych::getImie, KsiegaZmarlych::setImie,
                                        validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Nazwisko", ColumnType.STRING, KsiegaZmarlych::getNazwisko,
                                        KsiegaZmarlych::setNazwisko,
                                        validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Nazwisko Rodowe", ColumnType.STRING, KsiegaZmarlych::getNazwiskoRodowe,
                                        KsiegaZmarlych::setNazwiskoRodowe, validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Stan Cywilny", ColumnType.STRING, KsiegaZmarlych::getStanCywilny,
                                        KsiegaZmarlych::setStanCywilny, validators(NOT_EMPTY)),
                        new ColumnMeta<>("Data Urodzenia", ColumnType.DATE, KsiegaZmarlych::getDataUrodzenia,
                                        KsiegaZmarlych::setDataUrodzenia, validators(PAST_DATE, INVALID_FORMAT)),
                        new ColumnMeta<>("Miejsce Urodzenia", ColumnType.STRING, KsiegaZmarlych::getMiejsceUrodzenia,
                                        KsiegaZmarlych::setMiejsceUrodzenia, validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Data Zgonu", ColumnType.DATE, KsiegaZmarlych::getDataZgonu,
                                        KsiegaZmarlych::setDataZgonu,
                                        validators(PAST_DATE, INVALID_FORMAT)),
                        new ColumnMeta<>("Miejsce Zgonu", ColumnType.STRING, KsiegaZmarlych::getMiejsceZgonu,
                                        KsiegaZmarlych::setMiejsceZgonu, validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Ostatnie Miejsce Zamieszkania", ColumnType.STRING,
                                        KsiegaZmarlych::getOstatnieMiejsceZamieszkania,
                                        KsiegaZmarlych::setOstatnieMiejsceZamieszkania,
                                        validators(NOT_EMPTY)),
                        new ColumnMeta<>("Przyczyna zgonu", ColumnType.STRING,
                                        KsiegaZmarlych::getPrzyczynaZgonu, KsiegaZmarlych::setPrzyczynaZgonu,
                                        validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Dziecko Martwo Narodzone", ColumnType.BOOLEAN,
                                        KsiegaZmarlych::isDzieckoMartwoNarodzone,
                                        KsiegaZmarlych::setDzieckoMartwoNarodzone, validators(ANY_VALUE)),
                        new ColumnMeta<>("Choroba Zakaźna", ColumnType.BOOLEAN, KsiegaZmarlych::isChorobaZakazna,
                                        KsiegaZmarlych::setChorobaZakazna, validators(NOT_EMPTY)),
                        new ColumnMeta<>("Imię Ojca", ColumnType.STRING, KsiegaZmarlych::getImieOjca,
                                        KsiegaZmarlych::setImieOjca,
                                        validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Imię Matki", ColumnType.STRING, KsiegaZmarlych::getImieMatki,
                                        KsiegaZmarlych::setImieMatki, validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Nazwisko Ojca", ColumnType.STRING, KsiegaZmarlych::getNazwiskoOjca,
                                        KsiegaZmarlych::setNazwiskoOjca, validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Nazwisko Matki", ColumnType.STRING, KsiegaZmarlych::getNazwiskoMatki,
                                        KsiegaZmarlych::setNazwiskoMatki, validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Data Pochowania", ColumnType.DATE, KsiegaZmarlych::getDataPochowania,
                                        KsiegaZmarlych::setDataPochowania, validators(PAST_DATE, INVALID_FORMAT)),
                        new ColumnMeta<>("Data Ekshumacji", ColumnType.DATE, KsiegaZmarlych::getDataEkshumacji,
                                        KsiegaZmarlych::setDataEkshumacji, validators(PAST_DATE, INVALID_FORMAT)),
                        new ColumnMeta<>("Miejsce przed ekshumacją", ColumnType.STRING,
                                        KsiegaZmarlych::getMiejscePrzedEkshumacja,
                                        KsiegaZmarlych::setMiejscePrzedEkshumacja,
                                        validators(NOT_EMPTY)),
                        new ColumnMeta<>("Data Ponownego Pochówku", ColumnType.DATE,
                                        KsiegaZmarlych::getDataPonownegoPochowku,
                                        KsiegaZmarlych::setDataPonownegoPochowku,
                                        validators(PAST_DATE, INVALID_FORMAT)),
                        new ColumnMeta<>("Miejsce Ponownego Pochówku", ColumnType.STRING,
                                        KsiegaZmarlych::getMiejscePonownegoPochowku,
                                        KsiegaZmarlych::setMiejscePonownegoPochowku,
                                        validators(NOT_EMPTY)),
                        new ColumnMeta<>("Adres nowego cmentarza", ColumnType.STRING,
                                        KsiegaZmarlych::getAdresNowegoCmentarza,
                                        KsiegaZmarlych::setAdresNowegoCmentarza,
                                        validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Rodzaj grobu", ColumnType.STRING,
                                        KsiegaZmarlych::getRodzajGrobu, KsiegaZmarlych::setRodzajGrobu,
                                        validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Numer Aktu USC", ColumnType.STRING, KsiegaZmarlych::getNumerAktuUSC,
                                        KsiegaZmarlych::setNumerAktuUSC, validators(NOT_EMPTY)),
                        new ColumnMeta<>("Data USC", ColumnType.DATE, KsiegaZmarlych::getDataUSC,
                                        KsiegaZmarlych::setDataUSC,
                                        validators(PAST_DATE, INVALID_FORMAT)),
                        new ColumnMeta<>("Nazwa USC", ColumnType.STRING, KsiegaZmarlych::getNazwaUSC,
                                        KsiegaZmarlych::setNazwaUSC,
                                        validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Imię Dysponenta", ColumnType.STRING, KsiegaZmarlych::getImieDysponenta,
                                        KsiegaZmarlych::setImieDysponenta, validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Nazwisko Dysponenta", ColumnType.STRING,
                                        KsiegaZmarlych::getNazwiskoDysponenta,
                                        KsiegaZmarlych::setNazwiskoDysponenta, validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Organ", ColumnType.STRING, KsiegaZmarlych::getOrgan, KsiegaZmarlych::setOrgan,
                                        validators(NOT_EMPTY)),
                        new ColumnMeta<>("Pochowany w", ColumnType.STRING,
                                        KsiegaZmarlych::getPochowanyW, KsiegaZmarlych::setPochowanyW,
                                        validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("Uwagi", ColumnType.STRING, KsiegaZmarlych::getUwagi, KsiegaZmarlych::setUwagi,
                                        validators(NOT_EMPTY)),
                        new ColumnMeta<>("Uwagi, zastrzegający : imię i nazwisko, adres, dowód osobisty, seria numer",
                                        ColumnType.STRING, KsiegaZmarlych::getAdnotacja, KsiegaZmarlych::setAdnotacja,
                                        validators(NOT_EMPTY)),
                        new ColumnMeta<>("Miejsce pochowania", ColumnType.STRING,
                                        KsiegaZmarlych::getMiejscePochowania, KsiegaZmarlych::setMiejscePochowania,
                                        validators(NOT_EMPTY, ONLY_LETTERS)),
                        new ColumnMeta<>("LokalizacjaZDIZ", ColumnType.READ, KsiegaZmarlych::getLokalizacjaZDIZ,
                                        null,
                                        null),
                        new ColumnMeta<>("Opłata do", ColumnType.READ, KsiegaZmarlych::getGrobOplaconyDo,
                                        null,
                                        null),
                        new ColumnMeta<>("Cmentarz", ColumnType.READ,
                                        KsiegaZmarlych::getCemeteryFromCode, null,
                                        null),
                        new ColumnMeta<>("Rejon", ColumnType.READ,
                                        KsiegaZmarlych::getRegionFromCode, null,
                                        null),
                        new ColumnMeta<>("Kwatera", ColumnType.READ,
                                        KsiegaZmarlych::getQuarterFromCode, null,
                                        null),
                        new ColumnMeta<>("Rzad", ColumnType.READ,
                                        KsiegaZmarlych::getRowFromCode, null,
                                        null),
                        new ColumnMeta<>("Numer miejsca", ColumnType.READ,
                                        KsiegaZmarlych::getPlaceFromCode, null,
                                        null),
                        new ColumnMeta<>("Kod grobu", ColumnType.READ, KsiegaZmarlych::getGraveIdCode, null,
                                        null));

}
