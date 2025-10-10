package com.mycompany.sample.frontend.controls.MainView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FilterControls {
    private final Label cemeteryLabelName = new Label();
    private final ComboBox<String> graveTypeComboBox = new ComboBox<>();
    private final ComboBox<String> regionComboBox = new ComboBox<>();
    private final ComboBox<String> quarterComboBox = new ComboBox<>();
    private final TextField firstnameTextField = new TextField();
    private final TextField surnameTextField = new TextField();
    private final ObservableList<String> quarterList = FXCollections.observableArrayList();

    public Label getCemeteryLabelName() {
        return cemeteryLabelName;
    }

    public ComboBox<String> getGraveTypeComboBox() {
        return graveTypeComboBox;
    }

    public ComboBox<String> getRegionComboBox() {
        return regionComboBox;
    }

    public ComboBox<String> getQuarterComboBox() {
        return quarterComboBox;
    }

    public ObservableList<String> getQuarterList() {
        return quarterList;
    }

    public TextField getSurnameTextField() {
        return surnameTextField;
    }

    public TextField getFirstnameTextField() {
        return firstnameTextField;
    }

}
