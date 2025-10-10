package com.mycompany.sample.frontend.controls;

import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.backend.service.models.KsiegaGrobowService;
import com.mycompany.sample.backend.service.models.KsiegaZmarlychService;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EditDeceasedLocationControls {
    private final KsiegaZmarlychService deceasedService;
    private final KsiegaGrobowService graveService;

    private final KsiegaZmarlych deceased;
    Stage dialog = new Stage();

    private final Label nameLabel;
    private final Label actualCodeLabel;
    private final ComboBox<String> chooseType = new ComboBox<>();
    private final ComboBox<String> chooseRegion = new ComboBox<>();
    private final ComboBox<String> chooseQuarter = new ComboBox<>();
    private final ComboBox<String> chooseRow = new ComboBox<>();
    private final ComboBox<String> choosePlace = new ComboBox<>();
    private final Label summary = new Label();
    private String newCode = null;
    private final Button saveButton;
    private final Button cancelButton;

    public EditDeceasedLocationControls(KsiegaZmarlychService deceasedService, KsiegaGrobowService graveService, KsiegaZmarlych deceased) {
        this.deceasedService = deceasedService;
        this.graveService = graveService;
        this.deceased = deceased;

        nameLabel = new Label("Edytuj lokalizację dla: " + deceased.getImie() + " " + deceased.getNazwisko());
        actualCodeLabel = new Label("Obecny kod grobu: " + deceased.getGraveIdCode());

        chooseType.setPromptText("Typ miejsca");
        chooseType.getItems().addAll("Grób", "Kolumbarium", "Brak");

        chooseRegion.setPromptText("Rejon");
        chooseRegion.setDisable(true);

        chooseQuarter.setPromptText("Kwatera");
        chooseQuarter.setDisable(true);

        chooseRow.setPromptText("Rząd");
        chooseRow.setDisable(true);

        choosePlace.setPromptText("Miejsce");
        choosePlace.setDisable(true);

        summary.setVisible(false);
        summary.setManaged(false);

        saveButton = new Button("Zapisz");
        cancelButton = new Button("Wyjdź");

        setStyle();
    }

    private void setStyle() {
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        actualCodeLabel.setStyle("-fx-font-size: 16px;");
        summary.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #444;");
    }

    public ComboBox<String> getChooseType() {
        return chooseType;
    }

    public ComboBox<String> getChooseRegion() {
        return chooseRegion;
    }

    public ComboBox<String> getChooseQuarter() {
        return chooseQuarter;
    }

    public ComboBox<String> getChooseRow() {
        return chooseRow;
    }

    public ComboBox<String> getChoosePlace() {
        return choosePlace;
    }

    public Label getSummary() {
        return summary;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public KsiegaZmarlych getDeceased() {
        return deceased;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public Label getActualCodeLabel() {
        return actualCodeLabel;
    }

    public Stage getDialog() {
        return dialog;
    }

    public String getNewCode() {
        return newCode;
    }

    public void setNewCode(String newCode) {
        this.newCode = newCode;
    }

    public KsiegaZmarlychService getDeceasedService() {
        return deceasedService;
    }

    public KsiegaGrobowService getGraveService() {
        return graveService;
    }
}
