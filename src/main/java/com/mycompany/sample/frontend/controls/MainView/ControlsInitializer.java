package com.mycompany.sample.frontend.controls.MainView;

import com.mycompany.sample.backend.context.AppContext;

import javafx.scene.control.ScrollPane.ScrollBarPolicy;

public class ControlsInitializer {

    public static void initialize(MainControls c) {
        // Wstępny stan widoczności
        c.getTables().getDeceasedTable().setVisible(false);
        c.getTables().getGravesTable().setVisible(false);
        c.getTables().getLoadingIndicator().setVisible(false);

        // Etykieta cmentarza
        c.getFilters().getCemeteryLabelName().setText(AppContext.getSelectedCemeteryDisplayName());

        // ComboBoxy + prompty
        c.getFilters().getGraveTypeComboBox().getItems().addAll("Grób", "Kolumbarium", "Wszyscy");
        c.getFilters().getGraveTypeComboBox().setPromptText("Typ miejsca");

        c.getFilters().getRegionComboBox().setPromptText("Wybierz rejon");
        c.getFilters().getRegionComboBox().setDisable(true);

        c.getFilters().getQuarterComboBox().setPromptText("Wybierz kwaterę");
        c.getFilters().getQuarterComboBox().setDisable(true);

        c.getFilters().getFirstnameTextField().setPromptText("Podaj imie");
        c.getFilters().getFirstnameTextField().setDisable(true);

        c.getFilters().getSurnameTextField().setPromptText("Podaj nazwisko");
        c.getFilters().getSurnameTextField().setDisable(true);
        
        // Load na start nieaktywny
        c.getButtons().getLoadButton().setDisable(true);

        // Panel zmian (wysokości/scroll)
        c.getTables().getScrollPane().setPrefHeight(300);
        c.getTables().getScrollPane().setFitToWidth(true);
        c.getTables().getScrollPane().setVisible(false);
        c.getTables().getScrollPane().setManaged(false);
        c.getTables().getScrollPane().setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        // Paginacja
        c.getPagination().getPrevButton().setVisible(false);
        c.getPagination().getNextButton().setVisible(false);
        c.getPagination().getCurrentPageLabel().setVisible(false);

        // Save / View / Report
        c.getButtons().getSaveButton().setVisible(false);
        c.getButtons().getSaveButton().setDisable(true);

        c.getButtons().getChangeViewButton().setVisible(false);
        c.getButtons().getChangeViewButton().setDisable(true);

        c.getButtons().getGenerateReportButton().setDisable(true);
        c.getButtons().getGenerateReportButton().setVisible(false);
        c.getButtons().getGenerateReportButton().setManaged(false);
    }
}
