package com.mycompany.sample.frontend.controls;

import com.mycompany.sample.backend.service.models.KsiegaGrobowService;
import com.mycompany.sample.backend.service.models.KsiegaZmarlychService;

import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuControls {
    private final Stage primaryStage;
    private final KsiegaZmarlychService deceasedService;
    private final KsiegaGrobowService graveService;

    private final Button changeCemeteryButton = new Button("Zmień cmentarz");
    private final Button booksButton = new Button("Przeglądaj księgi");
    private final Button addGraveButton = new Button("Dodaj Grób");

    public MenuControls(Stage primaryStage, KsiegaZmarlychService deceasedService, KsiegaGrobowService graveService) {
        this.primaryStage = primaryStage;
        this.deceasedService = deceasedService;
        this.graveService = graveService;

        setStyle();
    }

    private void setStyle() {
        changeCemeteryButton.setMinWidth(200);
        booksButton.setMinWidth(200);
        addGraveButton.setMinWidth(200);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public KsiegaZmarlychService getDeceasedService() {
        return deceasedService;
    }

    public KsiegaGrobowService getGraveService() {
        return graveService;
    }

    public Button getChangeCemeteryButton() {
        return changeCemeteryButton;
    }

    public Button getBooksButton() {
        return booksButton;
    }

    public Button getAddGraveButton() {
        return addGraveButton;
    }

}
