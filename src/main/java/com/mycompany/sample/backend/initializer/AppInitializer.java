package com.mycompany.sample.backend.initializer;

import java.io.IOException;

import com.mycompany.sample.frontend.view.CemeterySelectionView;
import com.mycompany.sample.frontend.view.MainLayoutView;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AppInitializer {

    private final Stage primaryStage;

    public AppInitializer(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void startApp() throws IOException {
        Scene scene = new Scene(new StackPane(), 400, 200);
        scene.getStylesheets().add(MainLayoutView.class.getResource("/css/table.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Aplikacja");
        CemeterySelectionView.showSelection(primaryStage);
    }
}