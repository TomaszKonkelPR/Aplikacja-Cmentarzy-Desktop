package com.mycompany.sample.frontend.layout;

import com.mycompany.sample.frontend.controls.ExtendPayDeceasedControls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ExtendPayDeceasedLayoutBuilder {
    public static VBox getHeaderBox(ExtendPayDeceasedControls controls) {
        VBox box = new VBox(controls.getTitleLabel());
        box.setPadding(new Insets(8, 8, 0, 8));
        box.setAlignment(Pos.TOP_LEFT);
        return box;
    }

    public static VBox getCenterBox(ExtendPayDeceasedControls controls) {
        VBox fieldsBox = new VBox(10,
                controls.getR10(),
                controls.getR20(),
                controls.getDatePicker(),
                controls.getHintLabel()
        );
        fieldsBox.setAlignment(Pos.CENTER_LEFT);
        fieldsBox.setPadding(new Insets(20));
        return fieldsBox;
    }

    public static VBox getBottomBox(ExtendPayDeceasedControls controls) {
        HBox buttonBox = new HBox(10, controls.getConfirmButton(), controls.getCancelButton());
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(8));

        VBox bottomBox = new VBox(10, buttonBox);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.setPadding(new Insets(8));

        return bottomBox;
    }
}
