package com.mycompany.sample.frontend.components;

import java.util.List;
import java.util.Locale;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.backend.enums.ViewMode;
import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.util.RaportsUtils;
import com.mycompany.sample.frontend.util.deceased.DeceasedUpdate;
import com.mycompany.sample.frontend.util.loader.DeceasedDataLoader;
import com.mycompany.sample.frontend.util.loader.GravesDataLoader;
import com.mycompany.sample.frontend.view.MenuView;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

public class MainLayoutComponents {

    public static void setupBackButton(MainControls controls) {

        Button backButton = controls.getButtons().getBackButton();

        backButton.setOnAction(event -> {
            if (AlertUtils.confirmIfUnsavedChanges(controls)) {
                MenuView.showMenu(controls.getPrimaryStage(), controls.getGraveService(), controls.getDeceasedService());
            }
        });

    }

    public static void setupGraveTypeComboBox(MainControls controls) {

        ComboBox<String> graveTypeComboBox = controls.getFilters().getGraveTypeComboBox();

        graveTypeComboBox.setOnAction(event -> {
            String selectedGraveType = graveTypeComboBox.getValue();

            if (selectedGraveType != null) {

                ComboBox<String> regionComboBox = controls.getFilters().getRegionComboBox();
                ComboBox<String> quarterComboBox = controls.getFilters().getQuarterComboBox();
                TextField firstnameTextField = controls.getFilters().getFirstnameTextField();
                TextField surnameTextField = controls.getFilters().getSurnameTextField();

                regionComboBox.getSelectionModel().clearSelection();

                if (selectedGraveType.equals("Wszyscy")) {
                    regionComboBox.setDisable(true);
                    quarterComboBox.setDisable(true);
                    firstnameTextField.setDisable(false);
                    firstnameTextField.setText("");
                    surnameTextField.setDisable(false);
                    surnameTextField.setText("");
                    controls.getButtons().getLoadButton().setDisable(false);

                    controls.getPagination().getPrevButton().setDisable(true);
                    controls.getPagination().getNextButton().setDisable(true);
                    return;
                }

                // Dla Kolumbarium
                if (selectedGraveType.equals("Kolumbarium")) {
                    List<String> columbaria = controls.getGraveService()
                            .getListOfColumbariumByCemetery(AppContext.getSelectedCemetery());
                    regionComboBox.setItems(FXCollections.observableArrayList(columbaria));
                    regionComboBox.setDisable(false);
                    firstnameTextField.setDisable(true);
                    firstnameTextField.setText("");
                    surnameTextField.setDisable(true);
                    surnameTextField.setText("");
                }
                // Dla zwykłych grobów
                else {
                    List<String> regions = controls.getGraveService().getListOfRegionByCemetery(AppContext.getSelectedCemetery());
                    regionComboBox.setItems(FXCollections.observableArrayList(regions));
                    regionComboBox.setDisable(false);
                    firstnameTextField.setDisable(true);
                    firstnameTextField.setText("");
                    surnameTextField.setDisable(true);
                    surnameTextField.setText("");
                }

                quarterComboBox.getSelectionModel().clearSelection();
                controls.getButtons().getLoadButton().setDisable(true);

                controls.getPagination().getPrevButton().setDisable(true);
                controls.getPagination().getNextButton().setDisable(true);
            }
        });
    }

    public static void setupRegionComboBox(MainControls controls) {
        ComboBox<String> regionComboBox = controls.getFilters().getRegionComboBox();
        ComboBox<String> quarterComboBox = controls.getFilters().getQuarterComboBox();
        ObservableList<String> quarterList = controls.getFilters().getQuarterList();

        regionComboBox.setOnAction(event -> {
            String selectedRegion = regionComboBox.getValue();

            quarterComboBox.setDisable(true);
            quarterList.clear();
            quarterComboBox.setValue(null);

            if (selectedRegion != null) {
                List<String> quarters = controls.getGraveService().getListOfQuarterForRegionsByCemetery(
                        AppContext.getSelectedCemetery(), selectedRegion);

                quarterList.setAll(quarters);
                quarterComboBox.setDisable(false);

                controls.getPagination().getPrevButton().setDisable(true);
                controls.getPagination().getNextButton().setDisable(true);
            }
        });

        regionComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item.trim().isEmpty()) {
                    setText("Bez");
                } else {
                    setText(item);
                }
            }
        });
        regionComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item.trim().isEmpty()) ? "Bez" : item);
            }
        });
    }

    public static void setupQuarterComboBox(MainControls controls) {
        ComboBox<String> cb = controls.getFilters().getQuarterComboBox();
        cb.setEditable(true);
        cb.getStyleClass().add("normal-border");

        ObservableList<String> source = controls.getFilters().getQuarterList();
        FilteredList<String> filtered = new FilteredList<>(source, s -> true);
        cb.setItems(filtered);

        TextField editor = cb.getEditor();
        PauseTransition debounce = new PauseTransition(Duration.millis(250));
        BooleanProperty suspendFilter = new SimpleBooleanProperty(false);
        BooleanProperty programmatic = new SimpleBooleanProperty(false);

        // --- filtrowanie podczas pisania (case-insensitive, "contains") ---
        editor.textProperty().addListener((obs, old, text) -> {
            if (suspendFilter.get() || programmatic.get())
                return;
            debounce.stop();
            debounce.setOnFinished(e -> Platform.runLater(() -> {
                String q = (text == null ? "" : text.trim().toLowerCase(Locale.ROOT));

                // zapamiętaj bieżący wybór
                String selected = cb.getSelectionModel().getSelectedItem();

                filtered.setPredicate(item -> q.isEmpty()
                        || (item != null && item.toLowerCase(Locale.ROOT).contains(q)));

                // jeśli wybrany element zniknął z listy → wyczyść wybór (unikamy OOB)
                if (selected != null && !filtered.contains(selected)) {
                    cb.getSelectionModel().clearSelection();
                    cb.setValue(null);
                }

                if (!filtered.isEmpty() && cb.isFocused() && !cb.isShowing()) {
                    cb.show(); // pokaż listę wyników
                }
            }));
            debounce.playFromStart();
        });

        // --- po WYBORZE opcji: wpisz w edytor, pokaż pełną listę i zamknij popup ---
        cb.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            if (sel == null)
                return;
            suspendFilter.set(true);
            programmatic.set(true);
            Platform.runLater(() -> {
                editor.setText(sel);
                editor.positionCaret(sel.length());
                filtered.setPredicate(s -> true); // reset filtra, żeby wybór się nie zgubił
                cb.hide();
                programmatic.set(false);
                suspendFilter.set(false);
            });
        });

        // Enter wybiera pierwszy widoczny wynik; Strzałka w dół otwiera listę
        editor.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!filtered.isEmpty())
                    cb.getSelectionModel().select(filtered.get(0));
                cb.hide();
                e.consume();
            } else if (e.getCode() == KeyCode.DOWN) {
                if (!cb.isShowing())
                    cb.show();
            }
        });

        // Twój dotychczasowy handler "Pobierz"
        cb.setOnAction(event -> {
            String selected = cb.getValue();
            boolean ok = selected != null && source.contains(selected);
            controls.getButtons().getLoadButton().setDisable(!ok);
            controls.getFilters().getFirstnameTextField().setDisable(false);
            controls.getFilters().getSurnameTextField().setDisable(false);
            if (ok) {
                cb.getStyleClass().remove("error-border");
                if (!cb.getStyleClass().contains("normal-border"))
                    cb.getStyleClass().add("normal-border");
            }
        });

        // (opcjonalnie) przy focuse pokaż pełną listę, gdy edytor pusty
        cb.focusedProperty().addListener((o, was, is) -> {
            if (is && (editor.getText() == null || editor.getText().isBlank())) {
                filtered.setPredicate(s -> true);
                cb.show();
            }
        });
    }

    public static void setupLoadButton(MainControls controls) {

        controls.getButtons().getLoadButton().setOnAction(e -> {
            if (!AlertUtils.confirmIfUnsavedChanges(controls))
                return;

            controls.getViewState().clearChanges();

            var filters = controls.getFilters();
            String selectedGraveType = filters.getGraveTypeComboBox().getValue();
            String quarterValue = filters.getQuarterComboBox().getValue();

            if (!"Wszyscy".equals(selectedGraveType) && quarterValue == null) {
                AlertUtils.showError("Wystąpił błąd", "Musisz wybrać kwaterę!");
                return;
            }

            controls.getViewState().setViewState(true, null);

            DeceasedDataLoader.countDeceasedRecords(controls,
                    count -> {
                        controls.getPagination().reset(count.intValue());
                        if (count == 0) {
                            controls.getTables().getDeceasedData().clear();
                            controls.getTables().getGravesData().clear();
                            controls.getPagination().getPrevButton().setDisable(true);
                            controls.getPagination().getNextButton().setDisable(true);
                            controls.getPagination().getCurrentPageLabel().setText("Brak wyników");
                            controls.getViewState().setViewState(false, ViewMode.DECEASED);
                            return;
                        }
                        DeceasedDataLoader.loadDeceasedPage(controls);
                    },
                    ex -> {
                        System.err.println("Błąd liczenia rekordów: " + ex);
                        controls.getViewState().setViewState(false, ViewMode.DECEASED);
                    });
        });

        controls.getPagination().getPrevButton().setOnAction(e -> {
            if (!AlertUtils.confirmIfUnsavedChanges(controls))
                return;
            controls.getViewState().clearChanges();
            controls.getPagination().prevPage();
            DeceasedDataLoader.loadDeceasedPage(controls);
        });

        controls.getPagination().getNextButton().setOnAction(e -> {
            if (!AlertUtils.confirmIfUnsavedChanges(controls))
                return;
            controls.getViewState().clearChanges();
            controls.getPagination().nextPage();
            DeceasedDataLoader.loadDeceasedPage(controls);
        });
    }

    public static void setupSaveButton(MainControls controls) {
        Button saveButton = controls.getButtons().getSaveButton();

        saveButton.setOnAction(event -> {
            if (controls.getTables().getChangedDeceasedItems() == null
                    || controls.getTables().getChangedDeceasedItems().isEmpty()) {
                AlertUtils.showInfo("Brak zmian", "Nie ma zmian do zapisania.");
                return;
            }

            boolean confirmed = AlertUtils.showConfirmation(
                    "Potwierdzenie zapisu",
                    "Czy na pewno chcesz zapisać wprowadzone zmiany?");

            if (!confirmed) {
                return;
            }
            DeceasedUpdate.saveDeceasedChangesWithRefresh(controls);
        });
    }

    public static void setupChangeViewButton(MainControls controls) {
        Button changeViewButton = controls.getButtons().getChangeViewButton();
        changeViewButton.setOnAction(event -> {
            if (!AlertUtils.confirmIfUnsavedChanges(controls)) {
                return;
            }

            controls.getViewState().clearChanges();
            boolean showingDeceased = controls.getTables().getDeceasedTable().isVisible();

            if (showingDeceased) {
                if (controls.getTables().getGravesData().isEmpty()) {
                    GravesDataLoader.loadGraves(controls);
                } else {
                    controls.getViewState().setViewState(false, ViewMode.GRAVES);
                }
            } else {
                controls.getViewState().setViewState(false, ViewMode.DECEASED);
            }
        });
    }

    public static void setupGenerateReportButton(MainControls controls) {
        Button reportButton = controls.getButtons().getGenerateReportButton();
        
        reportButton.setOnAction(event -> {
            String region = controls.getFilters().getRegionComboBox().getValue();
            String quarter = controls.getFilters().getQuarterComboBox().getValue();

            List<KsiegaGrobow> graves = controls.getGraveService().getGravesByCemeteryAndQuarter(
                    AppContext.getSelectedCemetery(), region, quarter);

            if (graves == null || graves.isEmpty()) {
                AlertUtils.showInfo("Brak danych", "Brak danych do wygenerowania raportu!");
                return;
            }

            boolean confirmed = AlertUtils.showConfirmation(
                    "Potwierdzenie generowania raportu",
                    "Zostanie wygenerowany raport dla wybranych parametrów:\n"
                            + "Cmentarz: " + AppContext.getSelectedCemetery() + "\n"
                            + "Region: " + (region != null ? region : "—") + "\n"
                            + "Kwatera: " + (quarter != null ? quarter : "—") + "\n\n"
                            + "Czy chcesz kontynuować?");

            if (!confirmed) {
                return;
            }

            RaportsUtils.generateReportAsync(controls);
        });
    }

}
