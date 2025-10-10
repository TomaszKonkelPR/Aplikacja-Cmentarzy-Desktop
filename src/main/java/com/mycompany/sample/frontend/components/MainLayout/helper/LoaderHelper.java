package com.mycompany.sample.frontend.components.MainLayout.helper;

import com.mycompany.sample.backend.enums.ViewMode;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.util.loader.DeceasedDataLoader;

public class LoaderHelper {
    public static void handleCountResult(MainControls controls, Number count) {
        controls.getPagination().reset(count.intValue());

        if (count.intValue() == 0) {
            controls.getTables().getDeceasedData().clear();
            controls.getTables().getGravesData().clear();
            controls.getPagination().getPrevButton().setDisable(true);
            controls.getPagination().getNextButton().setDisable(true);
            controls.getPagination().getCurrentPageLabel().setText("Brak wyników");
            controls.getViewState().setViewState(false, ViewMode.DECEASED);
            return;
        }

        DeceasedDataLoader.loadDeceasedPage(controls);
    }

    public static void handleLoadError(MainControls controls, Throwable ex) {
        System.err.println("Błąd liczenia rekordów: " + ex);
        controls.getViewState().setViewState(false, ViewMode.DECEASED);
    }

    public static void loadPrevPage(MainControls controls) {
        controls.getPagination().getPrevButton().setOnAction(e -> {
            if (!AlertUtils.confirmIfUnsavedChanges(controls))
                return;
            controls.getViewState().clearChanges();
            controls.getPagination().prevPage();
            DeceasedDataLoader.loadDeceasedPage(controls);
        });
    }

    public static void loadNextPage(MainControls controls) {
        controls.getPagination().getNextButton().setOnAction(e -> {
            if (!AlertUtils.confirmIfUnsavedChanges(controls))
                return;
            controls.getViewState().clearChanges();
            controls.getPagination().nextPage();
            DeceasedDataLoader.loadDeceasedPage(controls);
        });
    }

}
