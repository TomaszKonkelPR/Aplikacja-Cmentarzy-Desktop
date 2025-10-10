package com.mycompany.sample.frontend.layout.initializer;

import com.mycompany.sample.frontend.controls.AddDeceasedControls;
import com.mycompany.sample.frontend.controls.AddGraveControls;
import com.mycompany.sample.frontend.controls.ExtendPayDeceasedControls;
import com.mycompany.sample.frontend.controls.MenuControls;
import com.mycompany.sample.frontend.controls.EditDeceasedLocationControls;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.layout.AddDeceasedLayoutBuilder;
import com.mycompany.sample.frontend.layout.AddGraveLayoutBuilder;
import com.mycompany.sample.frontend.layout.EditDeceasedLocationLayoutBuilder;
import com.mycompany.sample.frontend.layout.ExtendPayDeceasedLayoutBuilder;
import com.mycompany.sample.frontend.layout.MainLayoutBuilder;
import com.mycompany.sample.frontend.layout.MenuLayoutBuilder;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class UiInitializer {

    public static BorderPane initializeMenuLayout(MenuControls menuControls) {
        BorderPane root = new BorderPane();

        VBox sideBar = MenuLayoutBuilder.createSideBar(menuControls);
        root.setLeft(sideBar);

        VBox centerBox = MenuLayoutBuilder.createCenterBox();
        root.setCenter(centerBox);

        return root;
    }

    public static BorderPane initializeMainLayout(MainControls controls) {
        BorderPane root = new BorderPane();

        HBox topBar = MainLayoutBuilder.createTopBar(controls);
        root.setTop(topBar);

        StackPane centerPane = MainLayoutBuilder.createStackPane(controls);
        root.setCenter(centerPane);

        return root;
    }

    public static BorderPane initializeEditDeceasedLocationLayout(
            EditDeceasedLocationControls editDeceasedLocationControls) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        VBox titleBox = EditDeceasedLocationLayoutBuilder.getHeaderBox(editDeceasedLocationControls);
        root.setTop(titleBox);

        VBox centerBox = EditDeceasedLocationLayoutBuilder.getCenterBox(editDeceasedLocationControls);
        root.setCenter(centerBox);

        HBox buttonBox = EditDeceasedLocationLayoutBuilder.getButtonBox(editDeceasedLocationControls);
        root.setBottom(buttonBox);

        return root;
    }

    public static BorderPane initializeAddDeceasedLayout(AddDeceasedControls addDeceasedControls) {
        BorderPane root = new BorderPane();

        GridPane centerBox = AddDeceasedLayoutBuilder.getHeaderAndCenter(addDeceasedControls);
        root.setCenter(centerBox);

        HBox buttonBox = AddDeceasedLayoutBuilder.getButtonBox(addDeceasedControls);
        root.setBottom(buttonBox);

        return root;
    }

    public static BorderPane initializeAddGraveLayout(AddGraveControls controls) {
        BorderPane root = new BorderPane();

        VBox titleBox = AddGraveLayoutBuilder.getHeaderBox(controls);
        root.setTop(titleBox);

        VBox centerBox = AddGraveLayoutBuilder.getCenterBox(controls);
        root.setCenter(centerBox);

        VBox buttonBox = AddGraveLayoutBuilder.getBottomBox(controls);
        root.setBottom(buttonBox);

        return root;
    }

    public static BorderPane initializeExtendPayDeceasedLayout(ExtendPayDeceasedControls extendPayDeceasedControls) {
        BorderPane root = new BorderPane();

        VBox titleBox = ExtendPayDeceasedLayoutBuilder.getHeaderBox(extendPayDeceasedControls);
        root.setTop(titleBox);

        VBox centerBox = ExtendPayDeceasedLayoutBuilder.getCenterBox(extendPayDeceasedControls);
        root.setCenter(centerBox);

        VBox buttonBox = ExtendPayDeceasedLayoutBuilder.getBottomBox(extendPayDeceasedControls);
        root.setBottom(buttonBox);

        return root;
    }

}
