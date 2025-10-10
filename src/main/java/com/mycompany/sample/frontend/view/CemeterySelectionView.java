package com.mycompany.sample.frontend.view;

import java.io.IOException;

import com.mycompany.sample.frontend.configurator.UiConfigurator;
import com.mycompany.sample.frontend.controls.CemeterySelectionControls;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CemeterySelectionView {

    public static void showSelection(Stage primaryStage) throws IOException {
        CemeterySelectionControls cemeterySelectionControls = new CemeterySelectionControls(primaryStage);
        UiConfigurator.setupCemeterySelectionUiComponents(cemeterySelectionControls);

        Stage dialog = cemeterySelectionControls.getDialog();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Wybierz cmentarz");

        Label label = cemeterySelectionControls.getLabel();
        ComboBox<String> comboBox = cemeterySelectionControls.getCemeteryComboBox();
        Button confirmButton = cemeterySelectionControls.getConfirmButton();

        label.getStyleClass().add("dialog-label");
        comboBox.getStyleClass().add("dialog-combobox");
        confirmButton.getStyleClass().add("dialog-button");

        VBox layout = new VBox(15, label, comboBox, confirmButton);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("dialog-layout");

        Scene scene = new Scene(layout, 300, 180);
        scene.getStylesheets().add(CemeterySelectionView.class.getResource("/css/table.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

}
