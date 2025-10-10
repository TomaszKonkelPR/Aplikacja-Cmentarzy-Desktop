package com.mycompany.sample.frontend.controls;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.backend.service.models.KsiegaGrobowService;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AddGraveControls {
    private final Stage primaryStage;
    private final KsiegaGrobowService graveService;
    
    private final Stage dialog = new Stage();

    private final Label titleLabel = new Label("Dodajesz nowy grób dla " + AppContext.getSelectedCemeteryDisplayName());
    private final ComboBox<String> regionField = new ComboBox<>();
    private final ComboBox<String> quarterField = new ComboBox<>();
    private final ComboBox<String> rowField = new ComboBox<>();
    private final ComboBox<String> placeField = new ComboBox<>();

    private final Button saveButton = new Button("Zapisz");
    private final Button cancelButton = new Button("Anuluj");

    public AddGraveControls(Stage primaryStage, KsiegaGrobowService graveService) {
        this.primaryStage = primaryStage;
        this.graveService = graveService;
        setupFields();
    }

    private void setupFields() {
        regionField.setPromptText("Rejon");
        quarterField.setPromptText("Kwatera");
        rowField.setPromptText("Rząd");
        placeField.setPromptText("Miejsce");

        regionField.setEditable(true);
        quarterField.setEditable(true);
        rowField.setEditable(true);
        placeField.setEditable(true);

        regionField.setMaxWidth(200);
        quarterField.setMaxWidth(200);
        rowField.setMaxWidth(200);
        placeField.setMaxWidth(200);

        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Stage getDialog() {
        return dialog;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public ComboBox<String> getRegionField() {
        return regionField;
    }

    public ComboBox<String> getQuarterField() {
        return quarterField;
    }

    public ComboBox<String> getRowField() {
        return rowField;
    }

    public ComboBox<String> getPlaceField() {
        return placeField;
    }

    public KsiegaGrobowService getGraveService() {
        return graveService;
    }
}
