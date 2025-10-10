package com.mycompany.sample.frontend.components.MainLayout;

import java.util.List;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.backend.enums.ViewMode;
import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.frontend.components.MainLayout.helper.LoaderHelper;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.util.RaportsUtils;
import com.mycompany.sample.frontend.util.deceased.DeceasedUpdate;
import com.mycompany.sample.frontend.util.loader.DeceasedDataLoader;
import com.mycompany.sample.frontend.util.loader.GravesDataLoader;
import com.mycompany.sample.frontend.view.MenuView;

public class MainButtonComponents {
    public static void setupBackButton(MainControls controls) {
        controls.getButtons().getBackButton().setOnAction(event -> {
            if (AlertUtils.confirmIfUnsavedChanges(controls)) {
                MenuView.showMenu(controls.getPrimaryStage(),
                        controls.getGraveService(),
                        controls.getDeceasedService());
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

            DeceasedDataLoader.countDeceasedRecords(
                    controls,
                    count -> LoaderHelper.handleCountResult(controls, count),
                    ex -> LoaderHelper.handleLoadError(controls, ex));
        });

        LoaderHelper.loadPrevPage(controls);
        LoaderHelper.loadNextPage(controls);
    }

    public static void setupSaveButton(MainControls controls) {
        controls.getButtons().getSaveButton().setOnAction(event -> {
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
        controls.getButtons().getChangeViewButton().setOnAction(event -> {
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
        controls.getButtons().getGenerateReportButton().setOnAction(event -> {
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
