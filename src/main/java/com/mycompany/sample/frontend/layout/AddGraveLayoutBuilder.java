package com.mycompany.sample.frontend.layout;

import com.mycompany.sample.frontend.controls.AddGraveControls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AddGraveLayoutBuilder {

    public static VBox getHeaderBox(AddGraveControls controls) {
        VBox box = new VBox(controls.getTitleLabel());
        box.setPadding(new Insets(8, 8, 0, 8));
        box.setAlignment(Pos.TOP_LEFT);
        return box;
    }

    public static VBox getCenterBox(AddGraveControls controls) {
        VBox fieldsBox = new VBox(10,
                controls.getRegionField(),
                controls.getQuarterField(),
                controls.getRowField(),
                controls.getPlaceField()
        );
        fieldsBox.setAlignment(Pos.CENTER);
        fieldsBox.setPadding(new Insets(20));
        return fieldsBox;
    }

    
    public static VBox getBottomBox(AddGraveControls controls) {
        HBox buttonBox = new HBox(10, controls.getSaveButton(), controls.getCancelButton());
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(8));

        VBox bottomBox = new VBox(10, buttonBox);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(8));

        return bottomBox;
    }
}
