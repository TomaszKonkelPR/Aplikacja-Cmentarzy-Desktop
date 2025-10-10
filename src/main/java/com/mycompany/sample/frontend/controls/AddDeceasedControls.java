package com.mycompany.sample.frontend.controls;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.backend.service.models.KsiegaGrobowService;
import com.mycompany.sample.backend.service.models.KsiegaZmarlychService;
import com.mycompany.sample.frontend.components.tables.tableUtil.ColumnMeta;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class AddDeceasedControls {
    private final Stage owner;
    Stage dialog = new Stage();

    private KsiegaGrobow grave;
    private final KsiegaZmarlychService deceasedService;
    private final KsiegaGrobowService graveService;

    private final ObservableList<KsiegaZmarlych> items = FXCollections.observableArrayList();
    private final Map<String, Boolean> fieldValidMap = new HashMap<>();

    private final Label titleLabel;
    private final VBox deceasedListBox = new VBox(8);

    private final TextField firstnameTextField = new TextField();
    private final TextField surnameTextField = new TextField();
    private final Button searchButton = new Button("Wyszukaj");
    private final VBox searchResultsBox = new VBox();

    private TableView<KsiegaZmarlych> table = new TableView<>();;
    private final Button addRowButton = new Button("Dodaj wiersz");
    private final Button deleteRowButton = new Button("Usuń wiersz");
    private final Button saveButton = new Button("Zapisz");
    private final Button cancelButton = new Button("Anuluj");
    private final Button generateSingleRaportButton = new Button("Generuj pojedyńczy raport");
    private final Button closeGraveButton = new Button("Dodaj zamknięcie grobu");
    private final DatePicker closeDatePicker = new DatePicker();

    public AddDeceasedControls(Stage owner,
            KsiegaGrobow grave, KsiegaZmarlychService deceasedService, KsiegaGrobowService graveService) {
        this.owner = owner;
        this.grave = grave;
        this.deceasedService = deceasedService;
        this.graveService = graveService;

        this.titleLabel = new Label();
        this.titleLabel.setGraphic(formatGraveLabel(grave));

        closeDatePicker.setPromptText("Podaj date zlikwidowania grobu");
        closeDatePicker.setTooltip(new Tooltip("Podaj date likwidacji w formacie yyyy-MM-dd"));

        setStyle();
        saveButton.setDisable(true);
        deleteRowButton.setDisable(true);
        closeGraveButton.setDisable(true);
    }

    private void setStyle() {
        addRowButton.setId("view-button");
        deleteRowButton.setId("view-button");
        saveButton.setId("view-button");
        cancelButton.setId("view-button");
        generateSingleRaportButton.setId("view-button");
        closeGraveButton.setId("closeGrave-button");
        closeDatePicker.setMinWidth(250);
        titleLabel.setStyle("-fx-font-size: 16px;");
    }

    public static TextFlow formatGraveLabel(KsiegaGrobow g) {
        TextFlow flow = new TextFlow();

        if (g == null) {
            flow.getChildren().add(new Text(
                    "Rejon: -   Kwatera: -   Rząd: -   Miejsce: -   Ilość pochowanych: -   Oznaczenie ZDIZ: -"));
            return flow;
        }

        flow.getChildren().addAll(
                normal("Rejon: "), bold(v(g.getRejon())), new Text("   "),
                normal("Kwatera: "), bold(v(g.getKwatera())), new Text("   "),
                normal("Rząd: "), bold(v(g.getRzad())), new Text("   "),
                normal("Miejsce: "), bold(v(g.getNumerMiejsca())), new Text("   "),
                normal("Ilość pochowanych: "), bold(v(g.getIloscPochowanych())), new Text("   "),
                normal("Oznaczenie ZDIZ: "), bold(v(g.getLokalizacjaZDIZ())));

        return flow;
    }

    private static Text bold(String text) {
        Text t = new Text(text);
        t.setStyle("-fx-font-weight: bold;");
        return t;
    }

    private static Text normal(String text) {
        return new Text(text);
    }

    private static String v(Object o) {
        if (o == null)
            return "-";
        String s = String.valueOf(o).trim();
        return s.isEmpty() ? "-" : s;
    }

    public void initFieldValidMap(List<ColumnMeta<KsiegaZmarlych>> columns) {
        fieldValidMap.clear();
        for (ColumnMeta<KsiegaZmarlych> meta : columns) {
            fieldValidMap.put(meta.getTitle(), false);
        }
    }

    public void setFieldValid(String fieldName, boolean valid) {
        fieldValidMap.put(fieldName, valid);
    }

    public boolean areAllFieldsValid() {
        return fieldValidMap.values().stream().allMatch(Boolean::booleanValue);
    }

    public void setTable(TableView<KsiegaZmarlych> table) {
        this.table = table;
    }

    public Stage getOwner() {
        return owner;
    }

    public Stage getDialog() {
        return dialog;
    }

    public KsiegaGrobow getGrave() {
        return grave;
    }

    public ObservableList<KsiegaZmarlych> getItems() {
        return items;
    }

    public TableView<KsiegaZmarlych> getTable() {
        return table;
    }

    public Button getAddRowButton() {
        return addRowButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public Button getDeleteRowButton() {
        return deleteRowButton;
    }

    public TextField getFirstnameTextField() {
        return firstnameTextField;
    }

    public TextField getSurnameTextField() {
        return surnameTextField;
    }

    public Button getSearchButton() {
        return searchButton;
    }

    public VBox getSearchResultsBox() {
        return searchResultsBox;
    }

    public KsiegaZmarlychService getDeceasedService() {
        return deceasedService;
    }

    public KsiegaGrobowService getGraveService() {
        return graveService;
    }

    public VBox getDeceasedListBox() {
        return deceasedListBox;
    }

    public void setGrave(KsiegaGrobow grave) {
        this.grave = grave;
    }

    public Button getGenerateSingleRaportButton() {
        return generateSingleRaportButton;
    }

    public Button getCloseGraveButton() {
        return closeGraveButton;
    }

    public DatePicker getCloseDatePicker() {
        return closeDatePicker;
    }
}
