package com.mycompany.sample.frontend.util.graves;

import java.time.LocalDate;

import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.util.loader.GravesDataLoader;

import javafx.concurrent.Task;
import javafx.scene.control.Button;

public class ChangeGraveDataUtil {
    public static void updateAntiqueDate(KsiegaGrobow row, LocalDate newDate, MainControls controls, Button saveBtn) {

        saveBtn.setDisable(true);

        Task<Void> updateAntiqueDate = new Task<>() {
            @Override
            protected Void call() {
                controls.getGraveService().update(row.getId(), newDate);
                return null;
            }
        };

        updateAntiqueDate.setOnSucceeded(e -> {
            GravesDataLoader.loadGraves(controls);
        });

        updateAntiqueDate.setOnFailed(e -> {
            AlertUtils.showError("Błąd", "Nie udało się zmienić daty.");
            saveBtn.setDisable(false);
        });

        Thread t = new Thread(updateAntiqueDate, "updateAntiqueDate");
        t.setDaemon(true);
        t.start();
    }

    public static void updateGraveType(KsiegaGrobow row, String newType, MainControls controls, Button saveBtn) {

        saveBtn.setDisable(true);

        Task<Void> updateGraveType = new Task<>() {
            @Override
            protected Void call() {
                controls.getGraveService().updateGraveType(row.getId(), newType);
                return null;
            }
        };

        updateGraveType.setOnSucceeded(e -> {
            GravesDataLoader.loadGraves(controls);
        });

        updateGraveType.setOnFailed(e -> {
            AlertUtils.showError("Błąd", "Nie udało się zmienić typu grobu.");
            saveBtn.setDisable(false);
        });

        Thread t = new Thread(updateGraveType, "updateGraveType");
        t.setDaemon(true);
        t.start();
    }
}
