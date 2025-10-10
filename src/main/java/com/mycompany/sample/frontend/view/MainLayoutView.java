package com.mycompany.sample.frontend.view;

import com.mycompany.sample.frontend.binder.DataBinder;
import com.mycompany.sample.frontend.configurator.UiConfigurator;
import com.mycompany.sample.frontend.controls.MenuControls;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.layout.initializer.UiInitializer;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainLayoutView {

    public static Label showLoader(Stage primaryStage, String cemeteryName) {
        String message = "Łączenie z bazą " + cemeteryName + " ...";
        Label loadingLabel = new Label(message);
        loadingLabel.getStyleClass().add("loader-label");

        BorderPane loaderPane = new BorderPane();
        loaderPane.setCenter(loadingLabel);
        loaderPane.getStyleClass().add("loader-pane");

        StackPane appRoot = (StackPane) primaryStage.getScene().getRoot();
        appRoot.getChildren().setAll(loaderPane);

        primaryStage.setWidth(1600);
        primaryStage.setHeight(800);
        primaryStage.centerOnScreen();
        primaryStage.show();

        return loadingLabel;
    }

    public static void showMain(Stage primaryStage, MenuControls menuControls) {
        MainControls controls = new MainControls(primaryStage, menuControls.getDeceasedService(), menuControls.getGraveService());

        DataBinder.setupMainViewData(controls);

        UiConfigurator.setupUiComponents(controls);

        BorderPane mainRoot = UiInitializer.initializeMainLayout(controls);

        StackPane appRoot = (StackPane) primaryStage.getScene().getRoot();
        appRoot.getChildren().setAll(mainRoot);

        primaryStage.setTitle("Aplikacja Cmentarza");
        primaryStage.centerOnScreen();
    }
}
