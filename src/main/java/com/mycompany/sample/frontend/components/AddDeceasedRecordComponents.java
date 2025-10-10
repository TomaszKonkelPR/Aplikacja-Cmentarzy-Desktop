package com.mycompany.sample.frontend.components;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.mycompany.sample.frontend.components.tables.deceased.Add.DeceasedAddColumnDefinitions;
import com.mycompany.sample.frontend.controls.AddDeceasedControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.util.RaportsUtils;
import com.mycompany.sample.frontend.util.deceased.add.AddDeceasedSaverUtil;
import com.mycompany.sample.frontend.util.deceased.add.AddDeceasedSearchUtil;
import com.mycompany.sample.frontend.util.deceased.add.AddDeceasedTableUtil;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.util.StringConverter;

public class AddDeceasedRecordComponents {
    public static void setupSearchButton(AddDeceasedControls addDeceasedControls) {
        addDeceasedControls.getSearchButton().setOnAction(event -> {
            String surname = addDeceasedControls.getSurnameTextField().getText();
            if (surname == null) {
                AlertUtils.showError("Błąd wyszukiwania", "Nazwisko jest puste. Nie można wyszukać z pustym nazwiskiem");
                return;
            }
            AddDeceasedSearchUtil.loadDeceasedByNameAndSurname(addDeceasedControls);
        });
    }

    public static void setupAddRow(AddDeceasedControls addDeceasedControls) {
        addDeceasedControls.getAddRowButton().setOnAction(ev -> {
            AddDeceasedTableUtil.endInlineEdit(addDeceasedControls);
            if (!addDeceasedControls.getItems().isEmpty() && !AddDeceasedTableUtil.validateOrAlert(addDeceasedControls, "Nie można dodać nowego wiersza")) {
                return;
            }
            if (!AlertUtils.showConfirmation("Dodaj nowy wiersz", "Czy na pewno chcesz dodać nowy wiersz?")) {
                return;
            }
            AddDeceasedTableUtil.addNewRow(addDeceasedControls);
            AddDeceasedTableUtil.recomputeFieldValidMapFromRows(addDeceasedControls);
        });
    }

    public static void setupDeleteRowButton(AddDeceasedControls addDeceasedControls) {
        addDeceasedControls.getDeleteRowButton().setOnAction(ev -> {
            if (addDeceasedControls.getItems().size() <= 1) {
                AlertUtils.showError("Nie można usunąć", "Muszą istnieć co najmniej 2 wiersze, aby usunąć ostatni.");
                return;
            }

            var last = addDeceasedControls.getItems().get(addDeceasedControls.getItems().size() - 1);

            boolean hasData = DeceasedAddColumnDefinitions.COLUMNS_KSIEGA_ZMARLYCH.stream()
                    .filter(col -> !"Grób".equalsIgnoreCase(col.getTitle()))
                    .map(col -> col.getGetter().apply(last))
                    .anyMatch(val -> {
                        if (val == null) return false;
                        if (val instanceof String s) return !s.isBlank();
                        if (val instanceof Boolean b) return b;
                        if (val instanceof java.time.LocalDate || val instanceof java.util.Date) return true;
                        return true;
                    });

            String msg = hasData
                    ? "Ostatni wiersz zawiera dane.\nUsunięcie spowoduje ich utratę.\nCzy chcesz kontynuować?"
                    : "Czy na pewno chcesz usunąć ostatni wiersz?";

            if (!AlertUtils.showConfirmation("Usuń wiersz", msg)) {
                return;
            }

            if (!AddDeceasedTableUtil.deleteLastRow(addDeceasedControls)) {
                AlertUtils.showError("Nie można usunąć", "Operacja nie została wykonana.");
            }
        });
    }

    public static void setupSaveButton(AddDeceasedControls addDeceasedControls) {
        addDeceasedControls.getSaveButton().setOnAction(ev -> {
            AddDeceasedTableUtil.endInlineEdit(addDeceasedControls);

            if (!AddDeceasedTableUtil.validateOrAlert(addDeceasedControls, "Nie można zapisać")) {
                return;
            }

            if (!AlertUtils.showConfirmation("Potwierdź zapis", "Czy na pewno chcesz zapisać zmiany?")) {
                return;
            }

            AddDeceasedSaverUtil.saveNewDeceased(addDeceasedControls);
        });
    }

    public static void setupCancelButton(AddDeceasedControls addDeceasedControls) {
        addDeceasedControls.getCancelButton().setOnAction(ev -> {
            if (!addDeceasedControls.getItems().isEmpty()) {
                if (!AlertUtils.showConfirmation(
                        "Anuluj edycję",
                        "Masz niezapisane zmiany.\nJeśli kontynuujesz, zostaną utracone.\n\nCzy chcesz kontynuować?")) {
                    return;
                }
            }
            addDeceasedControls.getDialog().close();
        });
    }

    public static void setupCloseDatePicker(AddDeceasedControls addDeceasedControls) {
        DatePicker closeDatePicker = addDeceasedControls.getCloseDatePicker();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        closeDatePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String text) {
                if (text == null || text.isEmpty()) {
                    addDeceasedControls.getCloseGraveButton().setDisable(true);
                    return null;
                }
                try {
                    return LocalDate.parse(text, formatter);
                } catch (DateTimeParseException e) {
                    closeDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2;");
                    closeDatePicker.setTooltip(new Tooltip("Niepoprawna data, wpisz w formacie yyyy-MM-dd"));
                    addDeceasedControls.getCloseGraveButton().setDisable(true);
                    return null;
                }
            }
        });

        closeDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            addDeceasedControls.getCloseGraveButton().setDisable(false);

            closeDatePicker.setStyle("");
            closeDatePicker.setTooltip(new Tooltip("Podaj date likwidacji w formacie yyyy-MM-dd"));
        });

        closeDatePicker.setPromptText(addDeceasedControls.getCloseDatePicker().getPromptText());
    }

    public static void setupCloseGraveButton(AddDeceasedControls addDeceasedControls) {
        addDeceasedControls.getCloseGraveButton().setOnAction(ev -> {
            LocalDate closeDatePickerValue = addDeceasedControls.getCloseDatePicker().getValue();
            boolean alreadyClosed = addDeceasedControls.getGrave().getPochowani()
                    .stream()
                    .anyMatch(z -> "LIKWIDACJA".equalsIgnoreCase(z.getImie()) && "GROBU".equals(z.getNazwisko())
                            && closeDatePickerValue != null
                            && closeDatePickerValue.equals(z.getDataPochowania()));

            if (alreadyClosed) {
                AlertUtils.showError(
                        "Likwidacja o takiej dacie już istnieje",
                        "Dla tego grobu jest już wpis o likwidacji z datą: " + closeDatePickerValue);
                return;
            }

            if (closeDatePickerValue == null) {
                AlertUtils.showError("Błąd dodawania", "Musisz podać date likwidacji grobu.");
            }

            if (!AlertUtils.showConfirmation("Potwierdzenie likwidacji grobu", "Zostanie dodana data likwidacji grobu: "
                    + closeDatePickerValue + "\n" + "Czy chcesz kontynuować?")) {
                return;
            }

            AddDeceasedSaverUtil.saveCloseGrave(addDeceasedControls);
        });
    }

    public static void setupGenerateSingleRaportButton(AddDeceasedControls addDeceasedControls) {
        addDeceasedControls.getGenerateSingleRaportButton().setOnAction(ev -> {
            if (!AlertUtils.showConfirmation("Potwierdzenie generowania raportu",
                    "Zostanie wygenerowany raport tylko dla tego grobu\n" + "Czy chcesz kontynuować?")) {
                return;
            }

            RaportsUtils.generateSingleReportAsync(addDeceasedControls);
        });
    }
}
