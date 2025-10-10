package com.mycompany.sample.frontend.util;

import java.util.List;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.backend.enums.ViewMode;
import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.backend.service.raports.RaportGenerator;
import com.mycompany.sample.frontend.controls.AddDeceasedControls;
import com.mycompany.sample.frontend.controls.MainView.MainControls;

import javafx.concurrent.Task;

public class RaportsUtils {
    public static void generateReportAsync(MainControls controls) {
        String cemetery = AppContext.getSelectedCemetery();
        String region = controls.getFilters().getRegionComboBox().getValue();
        String quarter = controls.getFilters().getQuarterComboBox().getValue();
        controls.getViewState().setViewState(true, null);

        Task<Void> reportTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<KsiegaGrobow> graves = controls.getGraveService().getGravesByCemeteryAndQuarter(cemetery, region, quarter);
                if (graves == null || graves.isEmpty()) {
                    throw new IllegalStateException("Brak danych do wygenerowania raportu.");
                }
                RaportGenerator.generateReport(graves, region, quarter);
                return null;
            }
        };

        reportTask.setOnSucceeded(ev -> {
            controls.getViewState().setViewState(false, ViewMode.GRAVES);
            AlertUtils.showInfo("Sukces", "Raport został wygenerowany!");
        });

        reportTask.setOnFailed(ev -> {
            controls.getViewState().setViewState(false, ViewMode.GRAVES);
            Throwable ex = reportTask.getException();
            String msg = (ex != null && ex.getMessage() != null) ? ex.getMessage() : "Nieznany błąd.";
            AlertUtils.showError("Błąd", "Błąd podczas generowania raportu:\n" + msg);
            System.err.println("Błąd raportu: " + ex);
        });

        Thread t = new Thread(reportTask, "report-generator");
        t.setDaemon(true);
        t.start();
    }

    public static void generateSingleReportAsync(AddDeceasedControls addDeceasedControls) {
        addDeceasedControls.getGenerateSingleRaportButton().setDisable(true);
        Task<Void> reportTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                KsiegaGrobow grave = addDeceasedControls.getGraveService().findByGraveId(addDeceasedControls.getGrave().getId());
                if (grave == null) {
                    throw new IllegalStateException("Błąd id, nie znaleziono takiego");
                }
                List<KsiegaGrobow> graves = List.of(grave);
                RaportGenerator.generateReport(graves, grave.getRejon(), grave.getKwatera());
                return null;
            }
        };

        reportTask.setOnSucceeded(ev -> {
            addDeceasedControls.getGenerateSingleRaportButton().setDisable(false);
            AlertUtils.showInfo("Sukces", "Raport został wygenerowany!");
        });

        reportTask.setOnFailed(ev -> {
            Throwable ex = reportTask.getException();
            String msg = (ex != null && ex.getMessage() != null) ? ex.getMessage() : "Nieznany błąd.";
            AlertUtils.showError("Błąd", "Błąd podczas generowania raportu:\n" + msg);
            System.err.println("Błąd raportu: " + ex);
        });

        Thread t = new Thread(reportTask, "single-report-generator");
        t.setDaemon(true);
        t.start();
    }
}
