package com.mycompany.sample.frontend.layout;

import com.mycompany.sample.frontend.controls.MainView.MainControls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainLayoutBuilder {

    public static HBox createTopBar(MainControls controls) {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.getStyleClass().add("top-bar");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(controls.getFilters().getCemeteryLabelName(), controls.getFilters().getGraveTypeComboBox(),
                controls.getFilters().getRegionComboBox(),
                controls.getFilters().getQuarterComboBox(), 
                controls.getFilters().getFirstnameTextField(),
                controls.getFilters().getSurnameTextField(),
                 spacer, controls.getButtons().getLoadButton());
        return topBar;
    }

    public static StackPane createStackPane(MainControls controls) {
        VBox container = new VBox(5);
        VBox.setVgrow(controls.getTables().getDeceasedTable(), Priority.ALWAYS);

        StackPane tablesStack = new StackPane(controls.getTables().getDeceasedTable(), controls.getTables().getGravesTable());
        VBox.setVgrow(tablesStack, Priority.ALWAYS);

        VBox bottomPanel = createBottomPanel(controls);

        container.getChildren().addAll(tablesStack, bottomPanel);

        return new StackPane(container, controls.getTables().getLoadingIndicator());
    }

    private static VBox createBottomPanel(MainControls controls) {
        HBox buttonBar = createButtonBar(controls);
        buttonBar.getStyleClass().add("bottom-bar");

        VBox bottomPanel = new VBox(5, controls.getTables().getScrollPane(), buttonBar);
        bottomPanel.setAlignment(Pos.CENTER);

        return bottomPanel;
    }

    private static HBox createButtonBar(MainControls controls) {
        HBox leftButtons = new HBox(10, controls.getButtons().getBackButton(), controls.getPagination().getPrevButton(), controls.getPagination().getCurrentPageLabel(), controls.getPagination().getNextButton() );
        leftButtons.setAlignment(Pos.TOP_LEFT);
        leftButtons.setPadding(new Insets(0));

        HBox rightButtons = new HBox(10,
                controls.getButtons().getSaveButton(),
                controls.getButtons().getGenerateReportButton(),
                controls.getButtons().getChangeViewButton());
        rightButtons.setAlignment(Pos.BOTTOM_RIGHT);

        HBox buttonBar = new HBox();
        buttonBar.setPadding(new Insets(10, 10, 10, 10));
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.getStyleClass().add("bottom-bar");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        buttonBar.getChildren().addAll(leftButtons, spacer, rightButtons);
        return buttonBar;
    }

}
