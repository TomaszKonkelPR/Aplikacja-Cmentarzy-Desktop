package com.mycompany.sample.frontend.util.deceased.add;

import java.time.LocalDate;

import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.backend.service.models.KsiegaZmarlychService;
import com.mycompany.sample.frontend.controls.AddDeceasedControls;
import com.mycompany.sample.frontend.layout.addDeceased.AddDeceasedLayoutHelper;
import com.mycompany.sample.frontend.util.AlertUtils;

import javafx.concurrent.Task;
import javafx.scene.control.Button;

public class AddDeceasedSaverUtil {
    public static void handleAssignToGrave(KsiegaZmarlych deceased, AddDeceasedControls controls) {
        KsiegaZmarlychService deceasedService = controls.getDeceasedService();
        String code = controls.getGrave().getGraveIdCode();

        if (code != null && code.equals(deceased.getGraveIdCode())) {
            AlertUtils.showInfo("Informacja", null, "Zmarły \"" + deceased.getImie() + " " + deceased.getNazwisko()
                    + "\" jest już przypisany do wybranego grobu.");
            return;
        }

        boolean ok = showGraveChangeConfirmation(deceased, controls);
        if (!ok) return;

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

    private static boolean showGraveChangeConfirmation(KsiegaZmarlych deceased, AddDeceasedControls controls) {
        if (deceased.getGrave() != null && deceased.getGraveIdCode() != null && !deceased.getGraveIdCode().isBlank()) {
            String current = String.format("rejon %s, kwatera %s, rząd %s, miejsce %s",
                    deceased.getRegionFromCode(), deceased.getQuarterFromCode(),
                    deceased.getRowFromCode(), deceased.getPlaceFromCode());
            String target = String.format("rejon %s, kwatera %s, rząd %s, miejsce %s",
                    controls.getGrave().getRejon(), controls.getGrave().getKwatera(),
                    controls.getGrave().getRzad(), controls.getGrave().getNumerMiejsca());
            return AlertUtils.showConfirmation("Zmiana grobu",
                    "Zmarły jest już przypisany do grobu:\n" + current + "\nCzy chcesz zmienić na:\n" + target + "?");
        } else {
            return AlertUtils.showConfirmation("Zmiana grobu",
                    "Czy na pewno chcesz zmienić grób dla " + deceased.getImie() + " " + deceased.getNazwisko() + "?");
        }
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

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                deceasedService.saveAllForSingleGrave(addDeceasedControls.getItems(), addDeceasedControls.getGrave());
                return null;
            }
        };

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
    
}
