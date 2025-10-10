package com.mycompany.sample.frontend.controls.MainView;

import com.mycompany.sample.backend.service.models.KsiegaGrobowService;
import com.mycompany.sample.backend.service.models.KsiegaZmarlychService;

import javafx.stage.Stage;

public class MainControls {

    private final Stage primaryStage;
    private final KsiegaZmarlychService deceasedService;
    private final KsiegaGrobowService graveService;

    private final PaginationControls pagination;
    private final FilterControls filters;
    private final TableControls tables;
    private final ActionButtons buttons;
    private final ViewStateManager viewState;

    public MainControls(Stage primaryStage, KsiegaZmarlychService deceasedService, KsiegaGrobowService graveService) {
        this.primaryStage = primaryStage;
        this.deceasedService = deceasedService;
        this.graveService = graveService;

        this.pagination = new PaginationControls();
        this.filters = new FilterControls();
        this.tables = new TableControls();
        this.buttons = new ActionButtons();

        this.viewState = new ViewStateManager(this);

        ControlsInitializer.initialize(this);
        ControlsStyler.applyStyles(this);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public PaginationControls getPagination() {
        return pagination;
    }

    public FilterControls getFilters() {
        return filters;
    }

    public TableControls getTables() {
        return tables;
    }

    public ActionButtons getButtons() {
        return buttons;
    }

    public ViewStateManager getViewState() {
        return viewState;
    }

    public KsiegaZmarlychService getDeceasedService() {
        return deceasedService;
    }

    public KsiegaGrobowService getGraveService() {
        return graveService;
    }

    
}
