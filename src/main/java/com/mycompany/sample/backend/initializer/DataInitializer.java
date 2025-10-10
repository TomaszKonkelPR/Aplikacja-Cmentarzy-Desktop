package com.mycompany.sample.backend.initializer;

import java.nio.file.Path;

import com.mycompany.sample.backend.service.models.KsiegaGrobowService;
import com.mycompany.sample.backend.service.models.KsiegaZmarlychService;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class DataInitializer {

    public static void initialize(
            KsiegaGrobowService graveService,
            KsiegaZmarlychService deceasedService, String excelFile, Label statusLabel) throws Exception {
        Path appDataPath = ResourceInitializer.getAppDataDirectory();
        Path excelPath = appDataPath.resolve(excelFile);

        if (graveService.countGraves() == 0) {
            Platform.runLater(() -> statusLabel.setText("[INFO] Importuję dane do Księgi Grobów..."));
            graveService.importData(excelPath, statusLabel);
        }

        if (deceasedService.countDeceased() == 0) {
            Platform.runLater(() -> statusLabel.setText("[INFO] Importuję dane do Księgi Zmarłych..."));
            deceasedService.importData(excelPath, statusLabel);
        }
    }

}
