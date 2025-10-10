package com.mycompany.sample.frontend.components;

import java.io.IOException;

import com.mycompany.sample.frontend.controls.MenuControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.view.AddGraveView;
import com.mycompany.sample.frontend.view.CemeterySelectionView;
import com.mycompany.sample.frontend.view.MainLayoutView;

import javafx.scene.control.Button;

public class MenuLayoutComponents {
    public static void setupBooksButton(MenuControls menuUiControls) {
        Button booksButton = menuUiControls.getBooksButton();
        booksButton.setOnAction(e -> MainLayoutView.showMain(menuUiControls.getPrimaryStage(), menuUiControls));
    }

    public static void setupAddGraveButton(MenuControls menuUiControls) {
        Button addGraveButton = menuUiControls.getAddGraveButton();
        addGraveButton.setOnAction(event -> AddGraveView.showAddGrave(menuUiControls.getPrimaryStage(), menuUiControls));
    }

    public static void setupBackButton(MenuControls menuUiControls) {
        Button backButton = menuUiControls.getChangeCemeteryButton();
        backButton.setOnAction(e -> {
            try {
                CemeterySelectionView.showSelection(menuUiControls.getPrimaryStage());
            } catch (IOException ex) {
                ex.printStackTrace();
                AlertUtils.showError("Błąd", "Nie udało się wrócić do wyboru cmentarza: " + ex.getMessage());
            }
        });
    }

}
