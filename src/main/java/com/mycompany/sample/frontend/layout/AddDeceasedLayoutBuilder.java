package com.mycompany.sample.frontend.layout;

import java.util.List;

import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.controls.AddDeceasedControls;
import com.mycompany.sample.frontend.layout.addDeceased.AddDeceasedLayoutHelper;
import com.mycompany.sample.frontend.view.AddDeceasedRecordView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class AddDeceasedLayoutBuilder {
    public static GridPane getHeaderAndCenter(AddDeceasedControls c) {
        AddDeceasedLayoutHelper.refreshDeceasedList(c);

        ScrollPane deceasedScroll = createDeceasedScroll(c);
        HBox searchRow = createSearchRow(c);
        ScrollPane resultsScroll = createSearchResultsScroll(c);
        VBox tableBox = createTableBox(c);

        VBox topSection = new VBox(12,
                c.getTitleLabel(),
                deceasedScroll,
                searchRow,
                resultsScroll);
        topSection.setPadding(new Insets(12));
        topSection.setStyle("-fx-background-color: #ffffff;");

        return createGrid(topSection, tableBox);
    }

    private static ScrollPane createDeceasedScroll(AddDeceasedControls c) {
        VBox deceasedListBox = c.getDeceasedListBox();
        deceasedListBox.getChildren().clear();
        List<KsiegaZmarlych> pochowani = c.getGrave().getPochowani();

        if (pochowani == null || pochowani.isEmpty()) {
            Label noDeceasedLabel = new Label("Grób pusty");
            noDeceasedLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #999; -fx-font-size: 16px;");
            deceasedListBox.setAlignment(Pos.CENTER);
            deceasedListBox.getChildren().add(noDeceasedLabel);
        } else {
            deceasedListBox.setAlignment(Pos.TOP_LEFT);
            for (KsiegaZmarlych z : pochowani) {
                deceasedListBox.getChildren().add(AddDeceasedLayoutHelper.formatDeceasedInGraveBox(z));
            }
        }

        ScrollPane scroll = new ScrollPane(deceasedListBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPrefHeight(250);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    private static HBox createSearchRow(AddDeceasedControls c) {
        TextField firstnameField = c.getFirstnameTextField();
        TextField surnameField = c.getSurnameTextField();
        Button searchButton = c.getSearchButton();

        VBox firstnameBox = new VBox(4, new Label("Imię:"), firstnameField);
        VBox surnameBox = new VBox(4, new Label("Nazwisko:"), surnameField);
        firstnameBox.setPrefWidth(160);
        surnameBox.setPrefWidth(160);

        HBox searchRow = new HBox(12, firstnameBox, surnameBox, searchButton);
        searchRow.setAlignment(Pos.CENTER_RIGHT);
        searchRow.setPadding(new Insets(4, 0, 4, 0));
        return searchRow;
    }

    private static ScrollPane createSearchResultsScroll(AddDeceasedControls c) {
        VBox searchResultsBox = c.getSearchResultsBox();
        searchResultsBox.getChildren().clear();
        searchResultsBox.setSpacing(6);
        searchResultsBox.setPadding(new Insets(4));
        searchResultsBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ccc;");

        Label initialInfo = new Label("Wprowadź conajmniej nazwisko, aby wyszukać zmarłych.");
        initialInfo.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");
        searchResultsBox.getChildren().add(initialInfo);

        ScrollPane resultsScroll = new ScrollPane(searchResultsBox);
        resultsScroll.setFitToWidth(true);
        resultsScroll.setPrefHeight(120);
        VBox.setVgrow(resultsScroll, Priority.ALWAYS);

        return resultsScroll;
    }

    private static VBox createTableBox(AddDeceasedControls c) {
        VBox tableBox = new VBox(c.getTable());
        VBox.setVgrow(c.getTable(), Priority.ALWAYS);
        return tableBox;
    }

    private static GridPane createGrid(VBox topSection, VBox tableBox) {
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.add(topSection, 0, 0);
        grid.add(tableBox, 0, 1);

        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(100);
        grid.getColumnConstraints().add(cc);

        RowConstraints r1 = new RowConstraints();
        r1.setPercentHeight(75);
        RowConstraints r2 = new RowConstraints();
        r2.setPercentHeight(25);
        grid.getRowConstraints().addAll(r1, r2);

        GridPane.setVgrow(topSection, Priority.ALWAYS);
        GridPane.setVgrow(tableBox, Priority.ALWAYS);

        return grid;
    }

    public static HBox getButtonBox(AddDeceasedControls addDeceasedControls) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox box = new HBox(10, addDeceasedControls.getAddRowButton(), addDeceasedControls.getDeleteRowButton(),
                addDeceasedControls.getSaveButton(), addDeceasedControls.getCancelButton(), spacer,
                addDeceasedControls.getCloseDatePicker(), addDeceasedControls.getCloseGraveButton(),
                addDeceasedControls.getGenerateSingleRaportButton());
        box.setPadding(new Insets(8));
        box.getStylesheets().add(AddDeceasedRecordView.class.getResource("/css/table.css").toExternalForm());
        return box;
    }
}
