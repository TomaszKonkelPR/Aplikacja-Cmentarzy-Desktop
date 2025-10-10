package com.mycompany.sample.frontend.util.deceased;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.sample.backend.enums.ViewMode;
import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.util.loader.DeceasedDataLoader;

import javafx.concurrent.Task;
import javafx.scene.control.TableView;

public class DeceasedUpdate {
    public static void saveDeceasedChangesWithRefresh(MainControls controls) {
        TableView<KsiegaZmarlych> table = controls.getTables().getDeceasedTable();
        int sel = table.getSelectionModel().getSelectedIndex();

        controls.getViewState().setViewState(true, null);

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() {
                List<KsiegaZmarlych> deceasedToUpdate = new ArrayList<>(
                    controls.getTables().getChangedDeceasedItems().keySet());
                controls.getDeceasedService().updateAll(deceasedToUpdate);
                return null;
            }
        };

        saveTask.setOnSucceeded(ev -> {
            controls.getViewState().clearChanges();
            controls.getViewState().setViewState(false, ViewMode.DECEASED);

            DeceasedDataLoader.loadDeceasedPage(controls);

            AlertUtils.showInfo("Zapis zakończony", "Zmiany zostały zapisane w bazie danych.");

            table.requestFocus();
            if (sel >= 0) {
                table.scrollTo(sel);
            } else if (!table.getItems().isEmpty()) {
                table.scrollTo(0);
            }
            table.refresh();
        });

        saveTask.setOnFailed(ev -> {
            controls.getViewState().setViewState(false, ViewMode.DECEASED);
            AlertUtils.showError("Błąd zapisu", "Wystąpił problem podczas zapisu zmian.");
            System.err.println("Błąd zapisu: " + saveTask.getException());
        });

        Thread t = new Thread(saveTask, "update-deceased");
        t.setDaemon(true);
        t.start();
    }
}
