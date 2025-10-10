package com.mycompany.sample.frontend.util.deceased.add;

import java.util.List;

import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.backend.service.models.KsiegaZmarlychService;
import com.mycompany.sample.frontend.controls.AddDeceasedControls;
import com.mycompany.sample.frontend.layout.addDeceased.AddDeceasedLayoutHelper;
import com.mycompany.sample.frontend.util.AlertUtils;

import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

public class AddDeceasedSearchUtil {
    public static void loadDeceasedByNameAndSurname(AddDeceasedControls addDeceasedControls) {
        KsiegaZmarlychService deceasedService = addDeceasedControls.getDeceasedService();
        String firstname = addDeceasedControls.getFirstnameTextField().getText();
        String surname = addDeceasedControls.getSurnameTextField().getText();
        Button searchButton = addDeceasedControls.getSearchButton();
        VBox searchResultsBox = addDeceasedControls.getSearchResultsBox();

        ProgressIndicator loader = new ProgressIndicator();
        loader.setMaxSize(48, 48);
        searchResultsBox.getChildren().clear();
        searchResultsBox.getChildren().add(loader);

        searchButton.setText("Pobieranie...");
        searchButton.setDisable(true);

        Task<List<KsiegaZmarlych>> loadDeceasedByNameAndSurname = new Task<>() {
            @Override
            protected List<KsiegaZmarlych> call() {
                return deceasedService.getListOfDeceasedByNameAndSurname(firstname, surname);
            }
        };

        loadDeceasedByNameAndSurname.setOnSucceeded(e -> {
            List<KsiegaZmarlych> results = loadDeceasedByNameAndSurname.getValue();

            searchButton.setText("Wyszukaj");
            searchButton.setDisable(false);

            searchResultsBox.getChildren().clear();

            if (results.isEmpty()) {
                Label noResults = new Label("Nie znaleziono żadnych osób.");
                noResults.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");
                searchResultsBox.getChildren().add(noResults);
            } else {
                Label countLabel = new Label(
                        "Ilość znalezionych wyników: " + results.size());
                countLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 10 0;");
                searchResultsBox.getChildren().add(countLabel);
                results.forEach(z -> searchResultsBox.getChildren().add(
                        AddDeceasedLayoutHelper.formatFoundDeceasedBox(z, addDeceasedControls)));
            }
        });

        loadDeceasedByNameAndSurname.setOnFailed(e -> {
            searchButton.setText("Wyszukaj");
            searchButton.setDisable(false);
            Throwable ex = loadDeceasedByNameAndSurname.getException();
            AlertUtils.showError("Błąd pobierania", ex.getMessage());
        });

        Thread t = new Thread(loadDeceasedByNameAndSurname, "loadDeceasedByNameAndSurname");
        t.setDaemon(true);
        t.start();
    }

}
