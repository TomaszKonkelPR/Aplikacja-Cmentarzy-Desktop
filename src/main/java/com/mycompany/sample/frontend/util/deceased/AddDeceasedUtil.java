package com.mycompany.sample.frontend.util.deceased;

import java.time.LocalDate;
import java.util.List;

import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.backend.service.models.KsiegaZmarlychService;
import com.mycompany.sample.frontend.components.tables.deceased.Add.DeceasedAddColumnDefinitions;
import com.mycompany.sample.frontend.controls.AddDeceasedControls;
import com.mycompany.sample.frontend.layout.addDeceased.AddDeceasedLayoutHelper;
import com.mycompany.sample.frontend.util.AlertUtils;

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

public class AddDeceasedUtil {
    public static void loadDeceasedByNameAndSurname(AddDeceasedControls addDeceasedControls) {
        KsiegaZmarlychService deceasedService = addDeceasedControls.getDeceasedService();
        String firstname = addDeceasedControls.getFirstnameTextField().getText();
        String surname = addDeceasedControls.getSurnameTextField().getText();
        Button searchButton = addDeceasedControls.getSearchButton();
        VBox searchResultsBox = addDeceasedControls.getSearchResultsBox();

        ProgressIndicator loader = new ProgressIndicator();
        loader.setMaxSize(48, 48);
        searchResultsBox.getChildren().clear();
        searchResultsBox.getChildren().add(loader);

        searchButton.setText("Pobieranie...");
        searchButton.setDisable(true);

        Task<List<KsiegaZmarlych>> loadDeceasedByNameAndSurname = new Task<>() {
            @Override
            protected List<KsiegaZmarlych> call() {
                return deceasedService.getListOfDeceasedByNameAndSurname(firstname, surname);
            }
        };

        loadDeceasedByNameAndSurname.setOnSucceeded(e -> {
            List<KsiegaZmarlych> results = loadDeceasedByNameAndSurname.getValue();

            searchButton.setText("Wyszukaj");
            searchButton.setDisable(false);

            searchResultsBox.getChildren().clear();

            if (results.isEmpty()) {
                Label noResults = new Label("Nie znaleziono żadnych osób.");
                noResults.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");
                searchResultsBox.getChildren().add(noResults);
            } else {
                Label countLabel = new Label(
                        "Ilość znalezionych wyników: " + results.size());
                countLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 10 0;");
                searchResultsBox.getChildren().add(countLabel);

                for (KsiegaZmarlych z : results) {
                    Node box = AddDeceasedLayoutHelper.formatFoundDeceasedBox(z, addDeceasedControls);
                    searchResultsBox.getChildren().add(box);
                }
            }
        });

        loadDeceasedByNameAndSurname.setOnFailed(e -> {
            searchButton.setText("Wyszukaj");
            searchButton.setDisable(false);
            Throwable ex = loadDeceasedByNameAndSurname.getException();
            AlertUtils.showError("Błąd pobierania", ex.getMessage());
        });

        Thread t = new Thread(loadDeceasedByNameAndSurname, "loadDeceasedByNameAndSurname");
        t.setDaemon(true);
        t.start();
    }

    public static void handleAssignToGrave(KsiegaZmarlych deceased, AddDeceasedControls controls) {
        KsiegaZmarlychService deceasedService = controls.getDeceasedService();
        String code = controls.getGrave().getGraveIdCode();

        if (code != null && code.equals(deceased.getGraveIdCode())) {
            AlertUtils.showInfo("Informacja", null, "Zmarły \"" + deceased.getImie() + " " + deceased.getNazwisko()
                    + "\" jest już przypisany do wybranego grobu.");
            return;
        }

        if (deceased.getGrave() != null && deceased.getGraveIdCode() != null && !deceased.getGraveIdCode().isBlank()) {
            String currentGraveDesc = String.format("rejon %s, kwatera %s, rząd %s, miejsce %s",
                    deceased.getRegionFromCode(), deceased.getQuarterFromCode(),
                    deceased.getRowFromCode(), deceased.getPlaceFromCode());
            String targetGraveDesc = String.format("rejon %s, kwatera %s, rząd %s, miejsce %s",
                    controls.getGrave().getRejon(), controls.getGrave().getKwatera(),
                    controls.getGrave().getRzad(), controls.getGrave().getNumerMiejsca());

            boolean ok = AlertUtils.showConfirmation("Zmiana przypisanego grobu",
                    "Osoba \"" + deceased.getImie() + " " + deceased.getNazwisko() + "\" "
                            + "jest już przypisana do grobu:\n" + currentGraveDesc + "\n\n"
                            + "Czy na pewno chcesz zmienić na:\n" + targetGraveDesc + "?");
            if (!ok)
                return;
        } else {
            boolean ok = AlertUtils.showConfirmation("Zmiana przypisanego grobu",
                    "Czy na pewno chcesz zmienić grób dla " + deceased.getImie() + " " + deceased.getNazwisko() + "?");
            if (!ok)
                return;
        }

        Button searchButton = controls.getSearchButton();
        String prevBtnText = searchButton.getText();
        searchButton.setText("Zapisywanie...");
        searchButton.setDisable(true);

        Task<Void> updateCode = new Task<>() {
            @Override
            protected Void call() {
                deceasedService.updateCode(deceased.getId(), code);
                return null;
            }
        };

        updateCode.setOnSucceeded(e -> {
            searchButton.setText(prevBtnText);
            searchButton.setDisable(false);

            AddDeceasedLayoutHelper.refreshDeceasedList(controls);
            AddDeceasedLayoutHelper.refreshFoundDeceased(controls);

            AlertUtils.showInfo("Sukces", null, "Zmarły został przypisany do grobu.");
        });

        updateCode.setOnFailed(e -> {
            searchButton.setText(prevBtnText);
            searchButton.setDisable(false);
            controls.getSearchResultsBox().getChildren().clear();

            Throwable ex = updateCode.getException();
            String msg = (ex != null && ex.getMessage() != null) ? ex.getMessage() : "Nieznany błąd.";
            AlertUtils.showError("Błąd przypisywania", "Nie udało się przypisać zmarłego:\n" + msg);
        });

        Thread t = new Thread(updateCode, "UpdateCode");
        t.setDaemon(true);
        t.start();
    }

    public static void saveNewDeceased(AddDeceasedControls addDeceasedControls) {
        KsiegaZmarlychService deceasedService = addDeceasedControls.getDeceasedService();
        var addBtn = addDeceasedControls.getAddRowButton();
        var delBtn = addDeceasedControls.getDeleteRowButton();
        var saveBtn = addDeceasedControls.getSaveButton();
        var cnelBtn = addDeceasedControls.getCancelButton();
        var items = addDeceasedControls.getItems();

        addBtn.setDisable(true);
        delBtn.setDisable(true);
        saveBtn.setDisable(true);
        cnelBtn.setDisable(true);

        // Wątek do zapisu
        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // tu idzie zapis w tle
                deceasedService.saveAllForSingleGrave(addDeceasedControls.getItems(), addDeceasedControls.getGrave());
                return null;
            }
        };

        // Po udanym zakończeniu
        saveTask.setOnSucceeded(e -> {
            AddDeceasedLayoutHelper.refreshDeceasedList(addDeceasedControls);
            AddDeceasedLayoutHelper.refreshFoundDeceased(addDeceasedControls);
            AlertUtils.showInfo("Zapisano", "Dane zostały zapisane.");

            items.clear();
            addBtn.setDisable(false);
            delBtn.setDisable(false);
            saveBtn.setDisable(false);
            cnelBtn.setDisable(false);
        });

        // Obsługa błędów
        saveTask.setOnFailed(e -> {
            Throwable ex = saveTask.getException();
            String msg = (ex != null && ex.getMessage() != null && !ex.getMessage().isBlank())
                    ? ex.getMessage()
                    : (ex != null ? ex.getClass().getSimpleName() : "Nieznany błąd");
            AlertUtils.showError("Błąd zapisu", "Wystąpił błąd podczas zapisu: " + msg);
            addBtn.setDisable(false);
            delBtn.setDisable(false);
            saveBtn.setDisable(false);
            cnelBtn.setDisable(false);
        });

        // Uruchomienie wątku
        Thread t = new Thread(saveTask, "DeceasedSaveTask");
        t.setDaemon(true);
        t.start();
    }

    public static void saveCloseGrave(AddDeceasedControls addDeceasedControls) {
        LocalDate closeDatePickerValue = addDeceasedControls.getCloseDatePicker().getValue();
        addDeceasedControls.getCloseGraveButton().setDisable(true);

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                addDeceasedControls.getDeceasedService()
                        .saveCloseGrave(addDeceasedControls.getGrave(), closeDatePickerValue);
                return null;
            }
        };

        saveTask.setOnSucceeded(e -> {
            AddDeceasedLayoutHelper.refreshDeceasedList(addDeceasedControls);
            AlertUtils.showInfo("Zapisano", "Wpis od likwidacji został dodany");
            addDeceasedControls.getCloseGraveButton().setDisable(false);
        });

        saveTask.setOnFailed(e -> {
            Throwable ex = saveTask.getException();
            String msg = (ex != null && ex.getMessage() != null && !ex.getMessage().isBlank())
                    ? ex.getMessage()
                    : (ex != null ? ex.getClass().getSimpleName() : "Nieznany błąd");
            AlertUtils.showError("Błąd zapisu", "Wystąpił błąd podczas zapisu: " + msg);
            addDeceasedControls.getCloseGraveButton().setDisable(false);
        });

        Thread t = new Thread(saveTask, "close-grave-task");
        t.setDaemon(true);
        t.start();
    }

    public static void endInlineEdit(AddDeceasedControls addDeceasedControls) {
        if (addDeceasedControls.getTable().getEditingCell() != null) {
            addDeceasedControls.getTable().edit(-1, null);
        }
    }

    public static void addNewRow(AddDeceasedControls addDeceasedControls) {
        KsiegaZmarlych nowy = new KsiegaZmarlych();
        nowy.setGrave(addDeceasedControls.getGrave());
        addDeceasedControls.getItems().add(nowy);
        addDeceasedControls.getTable().getSelectionModel().select(nowy);
        addDeceasedControls.getTable().scrollTo(nowy);
        addDeceasedControls.getTable().refresh();
        refreshButtonsState(addDeceasedControls);
    }

    public static boolean deleteLastRow(AddDeceasedControls addDeceasedControls) {
        var items = addDeceasedControls.getItems();
        if (items.size() <= 1) {
            return false;
        }

        items.remove(items.size() - 1);

        recomputeFieldValidMapFromRows(addDeceasedControls);
        refreshButtonsState(addDeceasedControls);

        if (!items.isEmpty()) {
            var newLast = items.get(items.size() - 1);
            addDeceasedControls.getTable().getSelectionModel().select(newLast);
            addDeceasedControls.getTable().scrollTo(newLast);
        }
        return true;
    }

    public static void refreshButtonsState(AddDeceasedControls c) {
        boolean hasItems = !c.getItems().isEmpty();
        int size = c.getItems().size();
        c.getSaveButton().setDisable(!hasItems);
        c.getDeleteRowButton().setDisable(size <= 1);
    }

    // Waliduje całą tabelę. Jeśli są błędy – pokazuje szczegółowy alert i zwraca false. Gdy OK – zwraca true.
    public static boolean validateOrAlert(AddDeceasedControls addDeceasedControls, String errorTitle) {
        recomputeFieldValidMapFromRows(addDeceasedControls);

        if (addDeceasedControls.areAllFieldsValid()) {
            return true;
        }

        String details = collectValidationErrors(addDeceasedControls);
        AlertUtils.showError(errorTitle, details.isEmpty() ? "Uzupełnij wymagane pola." : details);
        return false;
    }

    // Przelicza mapę walidacji: kolumna = true, jeśli jest poprawna we wszystkich wierszach
    public static void recomputeFieldValidMapFromRows(AddDeceasedControls addDeceasedControls) {
        addDeceasedControls.initFieldValidMap(DeceasedAddColumnDefinitions.COLUMNS_KSIEGA_ZMARLYCH);
        DeceasedAddColumnDefinitions.COLUMNS_KSIEGA_ZMARLYCH.forEach(col -> {
            boolean allOkForColumn = addDeceasedControls.getItems().stream().allMatch(row -> {
                Object val = col.getGetter().apply(row);
                return DeceasedAddColumnDefinitions.getErrorMessage(col.getValidator(), val) == null;
            });
            addDeceasedControls.setFieldValid(col.getTitle(), allOkForColumn);
        });
    }

    // Zbiera pełną listę błędów (wiersz + kolumna + komunikat).
    private static String collectValidationErrors(AddDeceasedControls addDeceasedControls) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < addDeceasedControls.getItems().size(); i++) {
            final int rowNo = i + 1;
            KsiegaZmarlych row = addDeceasedControls.getItems().get(i);

            DeceasedAddColumnDefinitions.COLUMNS_KSIEGA_ZMARLYCH.forEach(col -> {
                Object val = col.getGetter().apply(row);
                String err = DeceasedAddColumnDefinitions.getErrorMessage(col.getValidator(), val);
                if (err != null) {
                    sb.append("Wiersz ").append(rowNo).append(": ")
                            .append(col.getTitle()).append(" → ").append(err).append("\n");
                }
            });
        }
        return sb.toString().trim();
    }
}
