package com.mycompany.sample.frontend.controls.MainView;

import javafx.scene.control.Button;

public class ActionButtons {
    private final Button backButton = new Button("Powrót");
    private final Button saveButton = new Button("Zapisz");
    private final Button loadButton = new Button("Załaduj dane");
    private final Button changeViewButton = new Button("Zmień widok");
    private final Button generateReportButton = new Button("Generuj raport");

    public Button getBackButton() { return backButton; }
    public Button getSaveButton() { return saveButton; }
    public Button getLoadButton() { return loadButton; }
    public Button getChangeViewButton() { return changeViewButton; }
    public Button getGenerateReportButton() { return generateReportButton; }
    
}
