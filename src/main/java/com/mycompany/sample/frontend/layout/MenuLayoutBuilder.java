package com.mycompany.sample.frontend.layout;

import java.util.List;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.frontend.controls.MenuControls;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MenuLayoutBuilder {
    public static VBox createSideBar(MenuControls menuControls) {
        Button booksButton = menuControls.getBooksButton();
        Button addGraveButton = menuControls.getAddGraveButton();
        Button changeCemeteryButton = menuControls.getChangeCemeteryButton();

        List<Button> buttons = List.of(booksButton, addGraveButton, changeCemeteryButton);
        buttons.forEach(button -> button.getStyleClass().add("sidebar-button"));

        VBox topButtons = new VBox(10, booksButton, addGraveButton);
        VBox bottomButton = new VBox(changeCemeteryButton);

        BorderPane internalPane = new BorderPane();
        internalPane.setTop(topButtons);
        internalPane.setBottom(bottomButton);

        VBox sideBar = new VBox(internalPane);
        sideBar.getStyleClass().add("sidebar");

        VBox.setVgrow(internalPane, Priority.ALWAYS);
        return sideBar;
    }

    public static VBox createCenterBox() {
        Label infoLabel = new Label("Zalogowano do bazy: " + AppContext.getSelectedCemeteryDisplayName());
        infoLabel.getStyleClass().add("info-label");

        VBox centerBox = new VBox(infoLabel);
        centerBox.getStyleClass().add("center-box");
        centerBox.setAlignment(Pos.CENTER_LEFT);

        return centerBox;
    }

}
