package com.mycompany.sample.frontend.view;

import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.configurator.UiConfigurator;
import com.mycompany.sample.frontend.controls.EditDeceasedLocationControls;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.layout.initializer.UiInitializer;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditDeceasedLocationView {
    public static void showEditDeceasedView(KsiegaZmarlych deceased, MainControls controls) {

        EditDeceasedLocationControls editDeceasedLocationControls = new EditDeceasedLocationControls(controls.getDeceasedService(), controls.getGraveService(), deceased);

        UiConfigurator.setupDeceasedEditLocationComponents(editDeceasedLocationControls, controls);

        BorderPane root = UiInitializer.initializeEditDeceasedLocationLayout(editDeceasedLocationControls);
        
        Stage dialog = editDeceasedLocationControls.getDialog();
        dialog.setTitle("Edytuj lokalizację");
        dialog.initOwner(controls.getPrimaryStage());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(new Scene(root, 800, 400));
        dialog.centerOnScreen();
        dialog.showAndWait();
    }
}
