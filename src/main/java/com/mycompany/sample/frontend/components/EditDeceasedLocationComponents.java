package com.mycompany.sample.frontend.components;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.controls.EditDeceasedLocationControls;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.util.deceased.DeceasedCodeUpdate;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;

public class EditDeceasedLocationComponents {

    private record ComboBoxes(
            ComboBox<String> type,
            ComboBox<String> region,
            ComboBox<String> quarter,
            ComboBox<String> row,
            ComboBox<String> place) {
    }

    private static ComboBoxes getBoxes(EditDeceasedLocationControls editDeceasedLocationControls) {
        return new ComboBoxes(
                editDeceasedLocationControls.getChooseType(),
                editDeceasedLocationControls.getChooseRegion(),
                editDeceasedLocationControls.getChooseQuarter(),
                editDeceasedLocationControls.getChooseRow(),
                editDeceasedLocationControls.getChoosePlace());
    }

    private static void resetComboBox(ComboBox<?> box) {
        box.getItems().clear();
        box.setValue(null);
        box.setDisable(true);
    }

    public static void setupChooseTypeComboBox(EditDeceasedLocationControls editDeceasedLocationControls) {
        var boxes = getBoxes(editDeceasedLocationControls);

        boxes.type().setOnAction(event -> {
            String type = boxes.type().getValue();
            resetComboBox(boxes.region());
            resetComboBox(boxes.quarter());
            resetComboBox(boxes.row());
            resetComboBox(boxes.place());

            if ("Kolumbarium".equals(type)) {
                boxes.region().setDisable(false);
                boxes.region().setItems(FXCollections.observableArrayList(
                        editDeceasedLocationControls.getGraveService().getListOfColumbariumByCemetery(AppContext.getSelectedCemetery())));
            } else if ("Grób".equals(type)) {
                boxes.region().setDisable(false);
                boxes.region().setItems(FXCollections.observableArrayList(
                        editDeceasedLocationControls.getGraveService().getListOfRegionByCemetery(AppContext.getSelectedCemetery())));
            } else if ("Brak".equalsIgnoreCase(type)) {
                boxes.region().setDisable(true);
                boxes.quarter().setDisable(true);
                boxes.row().setDisable(true);
                boxes.place().setDisable(true);
            }
            editDeceasedLocationControls.setNewCode("");
        });
    }

    public static void setupChooseRegionComboBox(EditDeceasedLocationControls editDeceasedLocationControls) {
        var boxes = getBoxes(editDeceasedLocationControls);

        boxes.region().setOnAction(event -> {
            resetComboBox(boxes.quarter());
            resetComboBox(boxes.row());
            resetComboBox(boxes.place());

            String region = boxes.region().getValue();
            if (region != null) {
                boxes.quarter().setDisable(false);
                boxes.quarter().setItems(FXCollections.observableArrayList(
                        editDeceasedLocationControls.getGraveService().getListOfQuarterForRegionsByCemetery(AppContext.getSelectedCemetery(), region)));
            }
            editDeceasedLocationControls.setNewCode("");
        });

        boxes.region().setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.trim().isEmpty()) {
                    setText("Bez");
                } else {
                    setText(item);
                }
            }
        });

        boxes.region().setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null || item.trim().isEmpty()) ? "Bez" : item);
            }
        });

    }

    public static void setupChooseQuarterComboBox(EditDeceasedLocationControls editDeceasedLocationControls) {
        var boxes = getBoxes(editDeceasedLocationControls);

        boxes.quarter().setOnAction(event -> {
            resetComboBox(boxes.row());
            resetComboBox(boxes.place());

            String region = boxes.region().getValue();
            String quarter = boxes.quarter().getValue();

            if (quarter != null) {
                boxes.row().setDisable(false);
                boxes.row().setItems(FXCollections.observableArrayList(
                        editDeceasedLocationControls.getGraveService().getListOfRowsByCemeteryAndQuarter(AppContext.getSelectedCemetery(), region,
                                quarter)));
            }
            editDeceasedLocationControls.setNewCode("");
        });
    }

    public static void setupChooseRowComboBox(EditDeceasedLocationControls editDeceasedLocationControls) {
        var boxes = getBoxes(editDeceasedLocationControls);

        boxes.row().setOnAction(event -> {
            resetComboBox(boxes.place());

            String region = boxes.region().getValue();
            String quarter = boxes.quarter().getValue();
            String row = boxes.row().getValue();

            if (quarter != null && row != null) {
                boxes.place().setDisable(false);
                boxes.place().setItems(FXCollections.observableArrayList(
                        editDeceasedLocationControls.getGraveService().getListOfPlacesByCemeteryAndQuarterAndRow(AppContext.getSelectedCemetery(), region,
                                quarter,
                                row)));
            }
            editDeceasedLocationControls.setNewCode("");
        });
    }

    public static void setupChoosePlaceComboBox(EditDeceasedLocationControls editDeceasedLocationControls) {
        var boxes = getBoxes(editDeceasedLocationControls);
        Label summary = editDeceasedLocationControls.getSummary();

        boxes.place().setOnAction(event -> {
            String type = boxes.type().getValue();
            String region = boxes.region().getValue();
            String quarter = boxes.quarter().getValue();
            String row = boxes.row().getValue();
            String place = boxes.place().getValue();

            if (quarter != null && row != null && place != null) {
                String code;
                String desc;
                boolean hasRegion = region != null && !region.isBlank();

                if ("Kolumbarium".equals(type)) {
                    code = String.join("/", AppContext.getSelectedCemetery(), region, quarter, row, place);
                    desc = String.format("Wybrano: Kolumbarium – %s /  %s / Rząd %s / Miejsce %s", region,
                            quarter, row, place);
                } else if (!hasRegion) {
                    code = String.join("/", AppContext.getSelectedCemetery(), quarter, row, place);
                    desc = String.format("Wybrano: Grób – Kwatera %s / Rząd %s / Miejsce %s", quarter, row, place);
                } else {
                    code = String.join("/", AppContext.getSelectedCemetery(), quarter + " " + region, row, place);
                    desc = String.format("Wybrano: Grób – Kwatera %s / Rejon %s / Rząd %s / Miejsce %s", quarter,
                            region, row, place);
                }
                editDeceasedLocationControls.setNewCode(code);

                summary.setText(desc + "\nNowy kod: " + code);
                summary.setVisible(true);
                summary.setManaged(true);
            } else {
                summary.setVisible(false);
                summary.setManaged(false);
                summary.setText("");
            }
        });
    }

    public static void setupSaveButton(EditDeceasedLocationControls editDeceasedLocationControls, MainControls controls) {
        Button saveButton = editDeceasedLocationControls.getSaveButton();
        saveButton.setOnAction(e -> {
            String newCodeInput = editDeceasedLocationControls.getNewCode();
            String selectedType = editDeceasedLocationControls.getChooseType().getValue();
            KsiegaZmarlych deceased = editDeceasedLocationControls.getDeceased();
            
            if ("Brak".equalsIgnoreCase(selectedType)) {
                String cemeteryCode = AppContext.getSelectedCemetery();
                newCodeInput = cemeteryCode + "///";
                editDeceasedLocationControls.setNewCode(newCodeInput);
            }

            if (deceased == null || newCodeInput == null || newCodeInput.isBlank()) {
                AlertUtils.showInfo(
                        "Brak danych",
                        "Proszę wybrać wszystkie opcje przed zapisaniem.");
                return;
            }
            System.out.println(newCodeInput);
            DeceasedCodeUpdate.updateGraveCode(editDeceasedLocationControls, controls);
        });
    }

    public static void setupCancelButton(EditDeceasedLocationControls editDeceasedLocationControls) {
        Stage dialog = editDeceasedLocationControls.getDialog();
        Button cancelButton = editDeceasedLocationControls.getCancelButton();
        cancelButton.setOnAction(e -> dialog.close());
    }
}
