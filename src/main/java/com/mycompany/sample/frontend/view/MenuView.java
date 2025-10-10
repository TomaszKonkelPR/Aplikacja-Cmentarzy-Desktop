package com.mycompany.sample.frontend.view;

import com.mycompany.sample.backend.service.models.KsiegaGrobowService;
import com.mycompany.sample.backend.service.models.KsiegaZmarlychService;
import com.mycompany.sample.frontend.configurator.UiConfigurator;
import com.mycompany.sample.frontend.controls.MenuControls;
import com.mycompany.sample.frontend.layout.initializer.UiInitializer;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MenuView {

    public static void showMenu(Stage primaryStage,
            KsiegaGrobowService graveService,
            KsiegaZmarlychService deceasedService) {

        MenuControls menuControls = new MenuControls(primaryStage, deceasedService, graveService);

        UiConfigurator.setupMenuUiComponents(menuControls);

        BorderPane menuRoot = UiInitializer.initializeMenuLayout(menuControls);

        StackPane appRoot = (StackPane) primaryStage.getScene().getRoot();
        appRoot.getChildren().setAll(menuRoot);

        primaryStage.setTitle("Menu główne");
        primaryStage.centerOnScreen();
    }

}
