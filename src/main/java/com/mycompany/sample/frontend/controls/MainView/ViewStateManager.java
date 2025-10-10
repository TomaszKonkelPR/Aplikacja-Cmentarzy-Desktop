package com.mycompany.sample.frontend.controls.MainView;

import com.mycompany.sample.backend.enums.ViewMode;

import javafx.scene.control.TableView;

public class ViewStateManager {

    private final MainControls c;

    public ViewStateManager(MainControls controls) {
        this.c = controls;
    }

    public void setViewState(boolean isLoading, ViewMode mode) {
        c.getTables().getLoadingIndicator().setVisible(isLoading);

        if (isLoading) {
            c.getTables().getDeceasedTable().setVisible(false);
            c.getTables().getGravesTable().setVisible(false);

            c.getFilters().getGraveTypeComboBox().setDisable(true);
            c.getFilters().getRegionComboBox().setDisable(true);
            c.getFilters().getQuarterComboBox().setDisable(true);
            c.getFilters().getFirstnameTextField().setDisable(true);
            c.getFilters().getSurnameTextField().setDisable(true);

            c.getPagination().getPrevButton().setVisible(false);
            c.getPagination().getNextButton().setVisible(false);
            c.getPagination().getCurrentPageLabel().setVisible(false);

            c.getButtons().getChangeViewButton().setDisable(true);
            c.getButtons().getGenerateReportButton().setDisable(true);
            c.getButtons().getBackButton().setDisable(true);
            c.getButtons().getLoadButton().setDisable(true);
            return;
        }

        if (mode == ViewMode.DECEASED) {
            c.getTables().getDeceasedTable().setVisible(true);
            c.getTables().getGravesTable().setVisible(false);

            c.getFilters().getGraveTypeComboBox().setDisable(false);
            String graveType = c.getFilters().getGraveTypeComboBox().getValue();
            boolean enableRegionAndQuarter = graveType != null && !graveType.equalsIgnoreCase("WSZYSCY");

            c.getFilters().getRegionComboBox().setDisable(!enableRegionAndQuarter);
            c.getFilters().getQuarterComboBox().setDisable(!enableRegionAndQuarter);
            c.getFilters().getFirstnameTextField().setDisable(false);
            c.getFilters().getSurnameTextField().setDisable(false);

            c.getPagination().getPrevButton().setVisible(true);
            c.getPagination().getNextButton().setVisible(true);
            c.getPagination().getCurrentPageLabel().setVisible(true);

            c.getButtons().getSaveButton().setVisible(true);
            c.getButtons().getSaveButton().setDisable(true);
            c.getButtons().getLoadButton().setDisable(false);
            c.getButtons().getBackButton().setDisable(false);
            c.getButtons().getChangeViewButton().setText("Pokaż groby");
            c.getButtons().getGenerateReportButton().setVisible(false);
            c.getButtons().getGenerateReportButton().setManaged(false);
        } else {
            c.getTables().getDeceasedTable().setVisible(false);
            c.getTables().getGravesTable().setVisible(true);

            c.getFilters().getGraveTypeComboBox().setDisable(false);
            String graveType = c.getFilters().getGraveTypeComboBox().getValue();
            boolean enableRegionAndQuarter = graveType != null && !graveType.equalsIgnoreCase("WSZYSCY");

            c.getFilters().getRegionComboBox().setDisable(!enableRegionAndQuarter);
            c.getFilters().getQuarterComboBox().setDisable(!enableRegionAndQuarter);
            c.getFilters().getFirstnameTextField().setDisable(false);
            c.getFilters().getSurnameTextField().setDisable(false);

            c.getPagination().getPrevButton().setVisible(false);
            c.getPagination().getNextButton().setVisible(false);
            c.getPagination().getCurrentPageLabel().setVisible(false);

            c.getButtons().getLoadButton().setDisable(false);
            c.getButtons().getSaveButton().setVisible(false);
            c.getButtons().getSaveButton().setDisable(true);
            c.getButtons().getBackButton().setDisable(false);
            c.getButtons().getChangeViewButton().setText("Pokaż zmarłych");
            c.getButtons().getGenerateReportButton().setDisable(false);
            c.getButtons().getGenerateReportButton().setVisible(true);
            c.getButtons().getGenerateReportButton().setManaged(true);
        }

        c.getButtons().getChangeViewButton().setVisible(true);
        c.getButtons().getChangeViewButton().setDisable(false);
    }

    public void clearChanges() {
        c.getTables().getChangedDeceasedItems().clear();
        c.getTables().getChangeGravesItems().clear();
        c.getTables().getChangesContainer().getChildren().clear();

        c.getTables().getScrollPane().setManaged(false);
        c.getTables().getScrollPane().setVisible(false);

        c.getButtons().getSaveButton().setVisible(false);
        c.getButtons().getSaveButton().setDisable(true);

        TableView<?> tv = c.getTables().getDeceasedTable();
        int sel = tv.getSelectionModel().getSelectedIndex();
        tv.requestFocus();
        if (sel >= 0) {
            tv.scrollTo(sel);
        } else if (!tv.getItems().isEmpty()) {
            tv.scrollTo(0);
        }
        tv.refresh();
    }
}
