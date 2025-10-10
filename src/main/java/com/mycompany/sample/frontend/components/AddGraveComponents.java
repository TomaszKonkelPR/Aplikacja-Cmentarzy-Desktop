package com.mycompany.sample.frontend.components;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.frontend.components.MainLayout.helper.ComboBoxHelper;
import com.mycompany.sample.frontend.controls.AddGraveControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.util.graves.AddGraveUtil;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

public class AddGraveComponents {
    private static boolean empty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static void setupRegionField(AddGraveControls addGraveControls) {
        ComboBox<String> regionField = addGraveControls.getRegionField();
        ComboBox<String> quarterField = addGraveControls.getQuarterField();
        ComboBox<String> rowField = addGraveControls.getRowField();
        ComboBox<String> placeField = addGraveControls.getPlaceField();
        
        ComboBoxHelper.configureNoneValue(regionField);

        regionField.setItems(FXCollections.observableArrayList(
                addGraveControls.getGraveService().getListOfAllRegionsByCemetery(AppContext.getSelectedCemetery())));

        regionField.setOnAction(ev -> {
            String region = regionField.getValue();
            if (region != null) {
                quarterField.setItems(FXCollections.observableArrayList(
                        addGraveControls.getGraveService().getListOfQuarterForRegionsByCemetery(AppContext.getSelectedCemetery(), region)));
            } else {
                quarterField.getItems().clear();
            }
            rowField.getItems().clear();
            placeField.getItems().clear();
        });
    }

    public static void setupQuarterField(AddGraveControls addGraveControls) {
        final String cemetery = AppContext.getSelectedCemetery();
        ComboBox<String> regionField = addGraveControls.getRegionField();
        ComboBox<String> quarterField = addGraveControls.getQuarterField();
        ComboBox<String> rowField = addGraveControls.getRowField();
        ComboBox<String> placeField = addGraveControls.getPlaceField();

        quarterField.setOnAction(ev -> {
            String region = regionField.getValue();
            String quarter = quarterField.getValue();
            if (region != null && quarter != null) {

                rowField.setItems(FXCollections.observableArrayList(
                        addGraveControls.getGraveService().getListOfRowsByCemeteryAndQuarter(cemetery, region, quarter)));
            } else {
                rowField.getItems().clear();
            }
            placeField.getItems().clear();
        });
    }

    public static void setupRowField(AddGraveControls addGraveControls) {
        final String cemetery = AppContext.getSelectedCemetery();
        ComboBox<String> regionField = addGraveControls.getRegionField();
        ComboBox<String> quarterField = addGraveControls.getQuarterField();
        ComboBox<String> rowField = addGraveControls.getRowField();
        ComboBox<String> placeField = addGraveControls.getPlaceField();

        rowField.setOnAction(ev -> {
            String region = regionField.getValue();
            String quarter = quarterField.getValue();
            String row = rowField.getValue();
            if (region != null && quarter != null && row != null) {
                placeField.setItems(FXCollections.observableArrayList(
                        addGraveControls.getGraveService().getListOfPlacesByCemeteryAndQuarterAndRow(cemetery, region, quarter, row)));
            } else {
                placeField.getItems().clear();
            }
        });

    }

    public static void setupSaveButton(AddGraveControls addGraveControls) {
        addGraveControls.getSaveButton().setOnAction(e -> {
            String quarter = addGraveControls.getQuarterField().getEditor().getText();
            String row = addGraveControls.getRowField().getEditor().getText();
            String place = addGraveControls.getPlaceField().getEditor().getText();
            String region = addGraveControls.getRegionField().getEditor().getText();

            if (empty(quarter) || empty(row) || empty(place)) {
                AlertUtils.showError("Niekompletne dane", "Uzupełnij pola: Kwatera, Rząd i Miejsce.");
                return;
            }

            StringBuilder msg = new StringBuilder();
            msg.append("Sprawdź dane miejsca:\n\n");
            if (!empty(region)) {
                msg.append("Region: ").append(region).append("\n");
            }
            msg.append("Kwatera: ").append(quarter).append("\n");
            msg.append("Rząd: ").append(row).append("\n");
            msg.append("Miejsce: ").append(place).append("\n");

            msg.append("\nCzy chcesz zapisać?");

            boolean confirmed = AlertUtils.showConfirmation("Potwierdzenie zapisu", msg.toString());
            if (!confirmed)
                return;

            AddGraveUtil.saveNewGrave(addGraveControls);
        });
    }

    public static void setupCancelButton(AddGraveControls addGraveControls) {
        addGraveControls.getCancelButton().setOnAction(e -> {
            boolean anyFilled = 
                    !empty(addGraveControls.getQuarterField().getEditor().getText()) ||
                    !empty(addGraveControls.getRowField().getEditor().getText()) ||
                    !empty(addGraveControls.getPlaceField().getEditor().getText());

            if (anyFilled) {
                boolean ok = AlertUtils.showConfirmation(
                        "Porzucić zmiany?",
                        "Masz wypełnione pola.\nCzy na pewno chcesz zamknąć bez zapisu?");
                if (!ok)
                    return;
            }
            addGraveControls.getDialog().close();
        });
    }

}
