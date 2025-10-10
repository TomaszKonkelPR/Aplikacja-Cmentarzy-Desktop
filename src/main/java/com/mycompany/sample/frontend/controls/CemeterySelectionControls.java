package com.mycompany.sample.frontend.controls;

import java.io.IOException;

import com.mycompany.sample.backend.database.DatabaseConfig;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CemeterySelectionControls {
    private final Stage primaryStage;
    DatabaseConfig config;
    Stage dialog = new Stage();

    Label label = new Label("Wybierz cmentarz:");
    ComboBox<String> cemeteryComboBox = new ComboBox<>();
    Button confirmButton = new Button("Dalej");

    public CemeterySelectionControls(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            this.config = DatabaseConfig.loadConfig(); 
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Nie udało się wczytać konfiguracji bazy danych", e);
        }
        confirmButton.setDisable(true);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Label getLabel() {
        return label;
    }

    public ComboBox<String> getCemeteryComboBox() {
        return cemeteryComboBox;
    }

    public Button getConfirmButton() {
        return confirmButton;
    }

    public DatabaseConfig getConfig() {
        return config;
    }

    public Stage getDialog() {
        return dialog;
    }

}
