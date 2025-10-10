package com.mycompany.sample;

import java.io.IOException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.backend.initializer.AppInitializer;
import com.mycompany.sample.backend.initializer.ResourceInitializer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {
    private AppInitializer appInitializer;
    private static final Logger log = LoggerFactory.getLogger("Startup");

    @Override
    public void init() {
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        ResourceInitializer.copyDefaultResourcesToAppData();

        log.info("Java: " + System.getProperty("java.version"));
        log.info("JavaFX: " + System.getProperty("javafx.runtime.version"));
        log.info("App Start");
        appInitializer = new AppInitializer(primaryStage);
        appInitializer.startApp();
    }

    @Override
    public void stop() {
        AppContext.shutdown();
        Platform.exit();
    }

    public static void main(String[] args) {
        Locale pl = Locale.of("pl", "PL");
        Locale.setDefault(pl);
        Locale.setDefault(Locale.Category.FORMAT, pl);
        Locale.setDefault(Locale.Category.DISPLAY, pl);
        log.info(Locale.getDefault().toString());
        launch(args);
    }

}
