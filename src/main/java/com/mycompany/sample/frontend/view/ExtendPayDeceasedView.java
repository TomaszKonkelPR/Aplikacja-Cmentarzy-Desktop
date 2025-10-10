package com.mycompany.sample.frontend.view;

import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.configurator.UiConfigurator;
import com.mycompany.sample.frontend.controls.ExtendPayDeceasedControls;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.layout.initializer.UiInitializer;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExtendPayDeceasedView {
    public static void showExtendPayDeceasedView(KsiegaZmarlych deceased, MainControls controls) {
        ExtendPayDeceasedControls extendPayDeceasedControls = new ExtendPayDeceasedControls(deceased, controls.getDeceasedService());

        UiConfigurator.setupExtendPayDeceasedComponents(extendPayDeceasedControls, controls);

        BorderPane root = UiInitializer.initializeExtendPayDeceasedLayout(extendPayDeceasedControls);

        Stage dialog = extendPayDeceasedControls.getDialog();
        dialog.setTitle("Przedłuż opłate grobu");
        dialog.initOwner(controls.getPrimaryStage());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(new Scene(root, 500, 250));
        dialog.centerOnScreen();
        dialog.showAndWait();
    }

}
