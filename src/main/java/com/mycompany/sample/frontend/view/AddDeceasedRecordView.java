package com.mycompany.sample.frontend.view;

import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.frontend.binder.DataBinder;
import com.mycompany.sample.frontend.configurator.UiConfigurator;
import com.mycompany.sample.frontend.controls.AddDeceasedControls;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.layout.initializer.UiInitializer;
import com.mycompany.sample.frontend.util.loader.GravesDataLoader;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class AddDeceasedRecordView {

    public static void showAddDeceasedRecord(
            Stage primaryStage,
            KsiegaGrobow grave,
            MainControls controls) {

        AddDeceasedControls addDeceasedControls = new AddDeceasedControls(primaryStage, grave, controls.getDeceasedService(),
                controls.getGraveService());

        DataBinder.setupAddDeceasedRecordData(addDeceasedControls);

        UiConfigurator.setupAddDeceasedRecordComponents(addDeceasedControls);

        BorderPane mainRoot = UiInitializer.initializeAddDeceasedLayout(addDeceasedControls);

        Stage dialog = addDeceasedControls.getDialog();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Dodawanie wpisów");
        dialog.setScene(new Scene(mainRoot, 1000, 600));
        dialog.setMaximized(true);

        dialog.setOnHidden(e -> {
            GravesDataLoader.loadGraves(controls);
        });
        dialog.showAndWait();
    }
}
