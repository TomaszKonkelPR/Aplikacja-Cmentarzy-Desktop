package com.mycompany.sample.frontend.components.MainLayout;

import java.util.List;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.frontend.components.MainLayout.helper.ComboBoxHelper;
import com.mycompany.sample.frontend.controls.MainView.MainControls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;

public class MainFilterComponents {
    public static void setupGraveTypeComboBox(MainControls controls) {
    ComboBox<String> graveTypeComboBox = controls.getFilters().getGraveTypeComboBox();

    graveTypeComboBox.setOnAction(event -> {
        String selectedGraveType = graveTypeComboBox.getValue();
        ComboBoxHelper.resetAllFilters(controls);

        if (selectedGraveType == null) return;

        if ("Wszyscy".equals(selectedGraveType)) {
            ComboBoxHelper.resetAllFilters(controls);

            controls.getFilters().getFirstnameTextField().setDisable(false);
            controls.getFilters().getSurnameTextField().setDisable(false);
            controls.getButtons().getLoadButton().setDisable(false);
            return;
        }

        List<String> items = "Kolumbarium".equals(selectedGraveType)
                ? controls.getGraveService().getListOfColumbariumByCemetery(AppContext.getSelectedCemetery())
                : controls.getGraveService().getListOfRegionByCemetery(AppContext.getSelectedCemetery());

        controls.getFilters().getRegionComboBox().setItems(FXCollections.observableArrayList(items));
        controls.getFilters().getRegionComboBox().setDisable(false);
    });
}

    public static void setupRegionComboBox(MainControls controls) {
        ComboBox<String> regionComboBox = controls.getFilters().getRegionComboBox();
        ComboBox<String> quarterComboBox = controls.getFilters().getQuarterComboBox();
        ObservableList<String> quarterList = controls.getFilters().getQuarterList();

        ComboBoxHelper.configureNoneValue(regionComboBox);

        regionComboBox.setOnAction(event -> {
            String selectedRegion = regionComboBox.getValue();

            quarterComboBox.setDisable(true);
            quarterList.clear();
            quarterComboBox.setValue(null);

            if (selectedRegion != null) {
                List<String> quarters = controls.getGraveService().getListOfQuarterForRegionsByCemetery(
                        AppContext.getSelectedCemetery(), selectedRegion);

                quarterList.setAll(quarters);
                quarterComboBox.setDisable(false);

                controls.getPagination().getPrevButton().setDisable(true);
                controls.getPagination().getNextButton().setDisable(true);
            }
        });

    }

    public static void setupQuarterComboBox(MainControls controls) {
        ComboBox<String> cb = controls.getFilters().getQuarterComboBox();
        ObservableList<String> source = controls.getFilters().getQuarterList();

        ComboBoxHelper.makeFilterable(cb, source);

        cb.setOnAction(event -> {
            String selected = cb.getValue();
            boolean ok = selected != null && source.contains(selected);
            controls.getButtons().getLoadButton().setDisable(!ok);
            controls.getFilters().getFirstnameTextField().setDisable(false);
            controls.getFilters().getSurnameTextField().setDisable(false);
        });

        cb.focusedProperty().addListener((o, was, is) -> {
            if (is && (cb.getEditor().getText() == null || cb.getEditor().getText().isBlank())) {
                ((FilteredList<String>) cb.getItems()).setPredicate(s -> true);
                cb.show();
            }
        });
    }

}
