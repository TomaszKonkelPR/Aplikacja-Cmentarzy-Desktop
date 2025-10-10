package com.mycompany.sample.frontend.util.loader;

import java.util.List;
import java.util.function.Consumer;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.backend.enums.ViewMode;
import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.backend.service.models.KsiegaZmarlychService;
import com.mycompany.sample.frontend.components.tables.tableUtil.TableUtils;
import com.mycompany.sample.frontend.controls.MainView.MainControls;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;

public class DeceasedDataLoader {
    public static void loadDeceasedPage(MainControls controls) {
        ObservableList<KsiegaZmarlych> deceasedData = controls.getTables().getDeceasedData();
        ObservableList<KsiegaGrobow> gravesData = controls.getTables().getGravesData();

        TableView<KsiegaZmarlych> table = controls.getTables().getDeceasedTable();
        int sel = table.getSelectionModel().getSelectedIndex();

        int pageIndex = controls.getPagination().getCurrentPageIndex();
        int pageSize = controls.getPagination().getPageSize();
        long total = controls.getPagination().getTotalRecords();

        controls.getViewState().setViewState(true, ViewMode.DECEASED);

        String cemetery = AppContext.getSelectedCemetery();
        String region = controls.getFilters().getRegionComboBox().getValue();
        String quarter = controls.getFilters().getQuarterComboBox().getValue();
        String graveType = controls.getFilters().getGraveTypeComboBox().getValue();
        String firstname = controls.getFilters().getFirstnameTextField().getText();
        String surname = controls.getFilters().getSurnameTextField().getText();

        Task<List<KsiegaZmarlych>> loadDeceasedPage = new Task<>() {
            @Override
            protected List<KsiegaZmarlych> call() {
                if ("Wszyscy".equals(graveType)) {
                    return controls.getDeceasedService().getSortedListOfDeceasedWithoutGrave(pageIndex, pageSize, firstname, surname);
                } else {
                    return controls.getDeceasedService().getSortedListOfDeceasedByCemeteryAndQuarter(
                            cemetery, region, quarter, firstname, surname, pageIndex, pageSize);
                }
            }
        };

        loadDeceasedPage.setOnSucceeded(ev -> {
            List<KsiegaZmarlych> list = loadDeceasedPage.getValue();
            deceasedData.setAll(list);
            gravesData.clear();

            long from = (long) pageIndex * pageSize + 1;
            long to = Math.min((long) (pageIndex + 1) * pageSize, total);

            controls.getPagination().getCurrentPageLabel()
                    .setText("Strona " + (pageIndex + 1) + " (" + from + "–" + to + " z " + total + ")");

            controls.getPagination().getPrevButton().setDisable(false);
            controls.getPagination().getNextButton().setDisable(false);

            TableUtils.resizeColumnsToContent(controls.getTables().getDeceasedTable());
            table.requestFocus();
            if (sel >= 0) {
                table.scrollTo(sel);
            } else if (!table.getItems().isEmpty()) {
                table.scrollTo(0);
            }
            table.refresh();
            controls.getViewState().setViewState(false, ViewMode.DECEASED);
        });

        loadDeceasedPage.setOnFailed(ev -> {
            System.err.println("Błąd ładowania strony: " + loadDeceasedPage.getException());
            controls.getViewState().setViewState(false, ViewMode.DECEASED);
        });

        Thread t = new Thread(loadDeceasedPage, "load-deceased");
        t.setDaemon(true);
        t.start();
    }

    public static void countDeceasedRecords(MainControls controls, Consumer<Long> onSuccess, Consumer<Throwable> onError) {
        KsiegaZmarlychService deceasedService = controls.getDeceasedService();
        String selectedGraveType = controls.getFilters().getGraveTypeComboBox().getValue();
        String regionValue = controls.getFilters().getRegionComboBox().getValue();
        String quarterValue = controls.getFilters().getQuarterComboBox().getValue();
        String firstname = controls.getFilters().getFirstnameTextField().getText();
        String surname = controls.getFilters().getSurnameTextField().getText();

        Task<Long> countTask = new Task<>() {
            @Override
            protected Long call() {
                if ("Wszyscy".equals(selectedGraveType)) {
                    return deceasedService.countListOfDeceasedWithoutGrave(firstname, surname);
                } else {
                    return deceasedService.countListOfDeceasedByPattern(
                            AppContext.getSelectedCemetery(), regionValue, quarterValue, firstname, surname);
                }
            }
        };

        countTask.setOnSucceeded(ev -> onSuccess.accept(countTask.getValue()));
        countTask.setOnFailed(ev -> onError.accept(countTask.getException()));

        Thread t = new Thread(countTask, "count-deceased");
        t.setDaemon(true);
        t.start();
    }
}
