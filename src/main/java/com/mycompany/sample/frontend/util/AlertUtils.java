package com.mycompany.sample.frontend.util;

import com.mycompany.sample.frontend.controls.MainView.MainControls;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class AlertUtils {
    public static boolean confirmIfUnsavedChanges(MainControls controls) {
        if (controls.getTables().getChangedDeceasedItems() != null && !controls.getTables().getChangedDeceasedItems().isEmpty()) {
            return showConfirmation(
                "Niezapisane zmiany",
                "Masz niezapisane zmiany.\nJeśli kontynuujesz, zostaną utracone.\n\nCzy chcesz kontynuować?"
            );
        }
        return true;
    }

    public static void showInfo(String title, String content) {
        showInfo(title, null, content);
    }

    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(okButton, cancelButton);

        return alert.showAndWait().filter(response -> response == okButton).isPresent();
    }

}
