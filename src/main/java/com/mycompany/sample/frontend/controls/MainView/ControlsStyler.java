package com.mycompany.sample.frontend.controls.MainView;

public class ControlsStyler {

    public static void applyStyles(MainControls c) {
        c.getButtons().getLoadButton().setId("load-button");
        c.getButtons().getSaveButton().setId("save-button");
        c.getButtons().getChangeViewButton().setId("view-button");
        c.getButtons().getGenerateReportButton().setId("view-button");
        c.getButtons().getBackButton().setId("back-button");
    }
}
