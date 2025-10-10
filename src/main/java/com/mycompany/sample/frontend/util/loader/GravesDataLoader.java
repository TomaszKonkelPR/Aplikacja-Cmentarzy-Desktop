package com.mycompany.sample.frontend.util.loader;

import java.util.List;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.backend.enums.ViewMode;
import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.frontend.components.tables.tableUtil.TableUtils;
import com.mycompany.sample.frontend.controls.MainView.MainControls;

import javafx.concurrent.Task;

public class GravesDataLoader {
    public static void loadGraves(MainControls controls) {

        controls.getViewState().setViewState(true, null);

        final String cemetery = AppContext.getSelectedCemetery();
        final String region = controls.getFilters().getRegionComboBox().getValue();
        final String quarter = controls.getFilters().getQuarterComboBox().getValue();

        Task<List<KsiegaGrobow>> loadGraves = new Task<>() {
            @Override
            protected List<KsiegaGrobow> call() {
                return controls.getGraveService().getGravesByCemeteryAndQuarter(cemetery, region, quarter);
            }
        };

        loadGraves.setOnSucceeded(ev -> {
            controls.getTables().getGravesData().setAll(loadGraves.getValue());
            TableUtils.resizeColumnsToContent(controls.getTables().getGravesTable());
            controls.getViewState().setViewState(false, ViewMode.GRAVES);
        });

        loadGraves.setOnFailed(ev -> {
            System.err.println("Błąd ładowania grobów: " + loadGraves.getException());
            controls.getViewState().setViewState(false, ViewMode.DECEASED);
        });

        Thread t = new Thread(loadGraves, "load-graves");
        t.setDaemon(true);
        t.start();
    }
}
