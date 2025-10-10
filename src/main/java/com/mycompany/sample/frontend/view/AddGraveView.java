package com.mycompany.sample.frontend.view;

import com.mycompany.sample.frontend.configurator.UiConfigurator;
import com.mycompany.sample.frontend.controls.AddGraveControls;
import com.mycompany.sample.frontend.controls.MenuControls;
import com.mycompany.sample.frontend.layout.initializer.UiInitializer;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddGraveView {
    public static void showAddGrave(Stage primaryStage, MenuControls menuUiControls) {
        AddGraveControls addGraveControls = new AddGraveControls(primaryStage, menuUiControls.getGraveService());

        UiConfigurator.setupAddGraveComponents(addGraveControls);

        BorderPane layout = UiInitializer.initializeAddGraveLayout(addGraveControls);

        Stage dialog = addGraveControls.getDialog();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Dodawanie grobu");
        dialog.setScene(new Scene(layout, 400, 300));
        dialog.showAndWait();
    }
}
