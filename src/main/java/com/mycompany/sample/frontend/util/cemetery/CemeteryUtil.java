package com.mycompany.sample.frontend.util.cemetery;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.backend.database.DatabaseConfig;
import com.mycompany.sample.backend.initializer.DataInitializer;
import com.mycompany.sample.frontend.controls.CemeterySelectionControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.view.MenuView;

import javafx.concurrent.Task;
import javafx.scene.control.Label;

public class CemeteryUtil {
    public static void connectToDatabase(DatabaseConfig config, String selectedCemetery, Label loadingLabel, CemeterySelectionControls cemeterySelectionControls) {
        Task<Void> initTask = new Task<>() {

            @Override
            protected Void call() throws Exception {
                Map<String, String> dbProps = config.getCredentialsFor(selectedCemetery);

                if (dbProps == null) {
                    throw new RuntimeException("Brak konfiguracji dla cmentarza: " + selectedCemetery);
                }

                AppContext.switchDatabase(dbProps);

                String excelFile = dbProps.get("excelFile");
                if (excelFile == null || excelFile.isBlank()) {
                    throw new RuntimeException("Brak pliku Excel dla cmentarza: " + selectedCemetery);
                }
                DataInitializer.initialize(AppContext.graveService(), AppContext.deceasedService(), excelFile,
                        loadingLabel);
                return null;
            }

            @Override
            protected void succeeded() {
                MenuView.showMenu(cemeterySelectionControls.getPrimaryStage(), AppContext.graveService(),
                        AppContext.deceasedService());
            }

            @Override
            protected void failed() {
                Throwable ex = getException();
                ex.printStackTrace();
                AppContext.shutdown();
                AlertUtils.showError("Błąd podczas inicjalizacji: ", ex.getMessage());
                cemeterySelectionControls.getPrimaryStage().close();
            }
        };

        Thread t = new Thread(initTask, "db-init");
        t.setDaemon(true);
        t.start();
    }

    public static final Map<String, String> cemeteryNameMap;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("LO", "Cmentarz Łostowicki");
        map.put("FR", "Cmentarz Franciszka");
        map.put("GA", "Cmentarz Garnizonowy");
        map.put("IG", "Cmentarz Ignacy");
        map.put("SA", "Cmentarz Salwator");
        map.put("SO", "Cmentarz Sobieszewo");
        map.put("JA", "Cmentarz Jadwigi");
        map.put("SW", "Cmentarz Św. Wojciech");
        map.put("OL", "Cmentarz Oliwa");
        map.put("SR", "Cmentarz Srebrzysko");
        map.put("KR", "Cmentarz Krakowiec");
        map.put("CN", "Kolumbarium Centralne");
        cemeteryNameMap = Collections.unmodifiableMap(map);
    }

    public static final Map<String, String> displayToCodeMap = cemeteryNameMap.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

}
