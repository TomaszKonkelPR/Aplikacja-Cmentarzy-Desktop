package com.mycompany.sample.frontend.layout.addDeceased;

import java.time.LocalDate;
import java.util.List;

import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.controls.AddDeceasedControls;
import com.mycompany.sample.frontend.util.deceased.add.AddDeceasedSaverUtil;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

public class AddDeceasedLayoutHelper {
    public static Node formatDeceasedInGraveBox(KsiegaZmarlych deceased) {
        VBox box = createBox(2);
        box.getChildren().addAll(
                createNameLabel(deceased),
                createDetailsLabel(deceased)
        );
        return box;
    }

    public static Node formatFoundDeceasedBox(KsiegaZmarlych deceased, AddDeceasedControls controls) {
        VBox box = createBox(6);
        box.getChildren().addAll(
                createNameLabel(deceased),
                createDetailsLabel(deceased),
                createGraveLabel(deceased),
                createAssignButton(deceased, controls)
        );
        return box;
    }

    private static VBox createBox(int spacing) {
        VBox box = new VBox(spacing);
        box.setPadding(new Insets(8));
        box.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #bbb; -fx-border-radius: 4;");
        return box;
    }

    private static Label createNameLabel(KsiegaZmarlych deceased) {
        String imie = optional(deceased.getImie());
        String nazwisko = optional(deceased.getNazwisko());
        Label label = new Label(imie + " " + nazwisko);
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        return label;
    }

    private static Label createDetailsLabel(KsiegaZmarlych deceased) {
        String ur = optionalDate(deceased.getDataUrodzenia());
        String zm = optionalDate(deceased.getDataZgonu());
        String poch = optionalDate(deceased.getDataPochowania());
        String uwagi = optional(deceased.getUwagi());
        String lokalizacjaZDIZ = optional(deceased.getLokalizacjaZDIZ());

        String details = String.format("ur. %s, zm. %s, poch. %s | UWAGI: %s | Lokalizacja ZDIZ: %s",
                ur, zm, poch, uwagi, lokalizacjaZDIZ);

        Label label = new Label(details);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");
        return label;
    }

    private static Label createGraveLabel(KsiegaZmarlych deceased) {
        String text = String.format(
                "Rejon - %s, Kwatera - %s, Rząd - %s, Miejsce - %s",
                optional(deceased.getRegionFromCode()),
                optional(deceased.getQuarterFromCode()),
                optional(deceased.getRowFromCode()),
                optional(deceased.getPlaceFromCode())
        );
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");
        return label;
    }

    private static Button createAssignButton(KsiegaZmarlych deceased, AddDeceasedControls controls) {
        Button button = new Button("Przypisz do tego grobu");
        button.setOnAction(e -> AddDeceasedSaverUtil.handleAssignToGrave(deceased, controls));
        return button;
    }

    private static String optional(String value) {
        return value != null && !value.isBlank() ? value : "-";
    }

    private static String optionalDate(LocalDate date) {
        return date != null ? date.toString() : "-";
    }

    public static void refreshDeceasedList(AddDeceasedControls addDeceasedControls) {
        String code = addDeceasedControls.getGrave().getGraveIdCode();
        KsiegaGrobow refreshed = addDeceasedControls
                .getGraveService()
                .findByGraveIdCode(code);
        if (refreshed != null) {
            addDeceasedControls.setGrave(refreshed);
        }

        VBox box = addDeceasedControls.getDeceasedListBox();
        box.getChildren().clear();

        KsiegaGrobow g = addDeceasedControls.getGrave();
        if (g != null && g.getPochowani() != null) {
            for (KsiegaZmarlych deceased : g.getPochowani()) {
                box.getChildren().add(formatDeceasedInGraveBox(deceased));
            }
        }
        addDeceasedControls.getTitleLabel().setGraphic(AddDeceasedControls.formatGraveLabel(g));
    }

    public static void refreshFoundDeceased(AddDeceasedControls addDeceasedControls) {
        VBox resultsBox = addDeceasedControls.getSearchResultsBox();
        resultsBox.getChildren().clear();

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(48, 48);
        resultsBox.getChildren().add(spinner);

        String firstname = addDeceasedControls.getFirstnameTextField().getText();
        String surname = addDeceasedControls.getSurnameTextField().getText();
        List<KsiegaZmarlych> results = addDeceasedControls.getDeceasedService()
                .getListOfDeceasedByNameAndSurname(firstname, surname);

        resultsBox.getChildren().clear();
        if (results.isEmpty()) {
            Label noResults = new Label("Nie znaleziono żadnych osób.");
            noResults.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");
            resultsBox.getChildren().add(noResults);
        } else {
            Label countLabel = new Label("Ilość znalezionych wyników: " + results.size());
            countLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 10 0;");
            resultsBox.getChildren().add(countLabel);
            for (KsiegaZmarlych deceased : results) {
                Node resultNode = formatFoundDeceasedBox(deceased, addDeceasedControls);
                resultsBox.getChildren().add(resultNode);
            }
        }
    }

}
