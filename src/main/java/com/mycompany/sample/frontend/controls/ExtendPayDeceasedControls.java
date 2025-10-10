package com.mycompany.sample.frontend.controls;

import java.time.LocalDate;

import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.backend.service.models.KsiegaZmarlychService;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

public class ExtendPayDeceasedControls {
    Stage dialog = new Stage();
    private final KsiegaZmarlych deceased;
    private final KsiegaZmarlychService deceasedService;
    
    Label titleLabel;

    Button confirmButton = new Button("Potwierdź");;
    Button cancelButton = new Button("Anuluj");

    CheckBox r10 = new CheckBox("10 lat");;
    CheckBox r20 = new CheckBox("20 lat");;

    DatePicker datePicker = new DatePicker();;
    Label hintLabel = new Label("Wybierz czas przedłużenia albo datę końcową");

    int years;
    LocalDate newDate;

    public ExtendPayDeceasedControls(KsiegaZmarlych deceased, KsiegaZmarlychService deceasedService) {
        this.deceased = deceased;
        this.deceasedService = deceasedService;

        titleLabel = new Label("Wybierz okres przedłużenia dla: " + deceased.getImie() + " " + deceased.getNazwisko());

        datePicker.setPromptText("Wybierz własną datę opłacenia");
        datePicker.setTooltip(new Tooltip("Podaj nową datę w formacie yyyy-MM-dd"));

        setStyle();
    }

    private void setStyle() {
        hintLabel.setStyle("-fx-text-fill: #777; -fx-font-size: 11px;");
        datePicker.setMinWidth(250);
    }

    public KsiegaZmarlych getDeceased() {
        return deceased;
    }

    public Stage getDialog() {
        return dialog;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public Button getConfirmButton() {
        return confirmButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public CheckBox getR10() {
        return r10;
    }

    public CheckBox getR20() {
        return r20;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public Label getHintLabel() {
        return hintLabel;
    }

    public int getYears() {
        return years;
    }

    public LocalDate getNewDate() {
        return newDate;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public void setNewDate(LocalDate newDate) {
        this.newDate = newDate;
    }

    public KsiegaZmarlychService getDeceasedService() {
        return deceasedService;
    }
}
