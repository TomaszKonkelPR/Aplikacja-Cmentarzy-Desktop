package com.mycompany.sample.frontend.components;

import java.util.List;
import java.util.stream.Collectors;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.backend.database.DatabaseConfig;
import com.mycompany.sample.frontend.controls.CemeterySelectionControls;
import com.mycompany.sample.frontend.util.cemetery.CemeteryUtil;
import com.mycompany.sample.frontend.view.MainLayoutView;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class CemeterySelectionLayoutComponents {

    public static void setupCemeteryComboBox(CemeterySelectionControls cemeterySelectionControls) {
        ComboBox<String> cemeteryComboBox = cemeterySelectionControls.getCemeteryComboBox();
        Button confirmButton = cemeterySelectionControls.getConfirmButton();
        DatabaseConfig config = cemeterySelectionControls.getConfig();
        List<String> fullNames = config.getAvailableCemeteries().stream()
                .map(code -> CemeteryUtil.cemeteryNameMap.getOrDefault(code, code))
                .collect(Collectors.toList());
        cemeteryComboBox.setItems(FXCollections.observableArrayList(fullNames));
        cemeteryComboBox.setPromptText("Wybierz cmentarz");

        cemeteryComboBox.setOnAction(e -> {
            confirmButton.setDisable(cemeteryComboBox.getValue() == null);
        });

    }

    public static void setupConfirmButton(CemeterySelectionControls cemeterySelectionControls) {
        DatabaseConfig config = cemeterySelectionControls.getConfig();
        ComboBox<String> cemeteryComboBox = cemeterySelectionControls.getCemeteryComboBox();
        Button confirmButton = cemeterySelectionControls.getConfirmButton();
        confirmButton.setOnAction(e -> {
            AppContext.setSelectedCemetery(null);
            String selectedName = cemeteryComboBox.getValue();
            String selectedCemetery = CemeteryUtil.displayToCodeMap.get(selectedName);
            if (selectedCemetery != null) {
                cemeterySelectionControls.getDialog().close();
                AppContext.setSelectedCemetery(null);
                AppContext.setSelectedCemetery(selectedCemetery, selectedName);
                Label loadingLabel = MainLayoutView.showLoader(cemeterySelectionControls.getPrimaryStage(),
                        selectedName);

                CemeteryUtil.connectToDatabase(config, selectedCemetery, loadingLabel, cemeterySelectionControls);
            }
        });
    }
}
