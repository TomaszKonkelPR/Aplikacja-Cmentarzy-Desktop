package com.mycompany.sample.frontend.layout;

import com.mycompany.sample.frontend.controls.EditDeceasedLocationControls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class EditDeceasedLocationLayoutBuilder {
    public static VBox getHeaderBox(EditDeceasedLocationControls editDeceasedLocationControls) {
        VBox titleBox = new VBox(5, editDeceasedLocationControls.getNameLabel(),
                editDeceasedLocationControls.getActualCodeLabel());
        titleBox.setAlignment(Pos.CENTER);
        return titleBox;
    }

    public static VBox getCenterBox(EditDeceasedLocationControls editDeceasedLocationControls) {
        HBox comboBoxRow = new HBox(
                20,
                editDeceasedLocationControls.getChooseType(),
                editDeceasedLocationControls.getChooseRegion(),
                editDeceasedLocationControls.getChooseQuarter(),
                editDeceasedLocationControls.getChooseRow(),
                editDeceasedLocationControls.getChoosePlace());
        comboBoxRow.setAlignment(Pos.CENTER);

        Label summaryLabel = editDeceasedLocationControls.getSummary(); 

        VBox centerBox = new VBox(10, comboBoxRow, summaryLabel);
        centerBox.setAlignment(Pos.CENTER);

        return centerBox;
    }

    public static HBox getButtonBox(EditDeceasedLocationControls editDeceasedLocationControls) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonBox = new HBox(10, editDeceasedLocationControls.getCancelButton(), spacer,
                editDeceasedLocationControls.getSaveButton());
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        return buttonBox;
    }

}
