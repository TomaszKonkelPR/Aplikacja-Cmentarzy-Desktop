package com.mycompany.sample.frontend.util.graves;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.backend.enums.GraveAddType;
import com.mycompany.sample.backend.generator.GenerateCodeFromGrave;
import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.frontend.controls.AddGraveControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.util.text.normalizer.NormalizerText;

import javafx.concurrent.Task;

public class AddGraveUtil {
    public static void saveNewGrave(AddGraveControls addGraveControls) {
        KsiegaGrobow grave = buildGrave(addGraveControls);
        addGraveControls.getSaveButton().setDisable(true);

        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return addGraveControls.getGraveService().saveNewGrave(grave);
            }
        };

        task.setOnSucceeded(ev -> {
            addGraveControls.getSaveButton().setDisable(false);
            AlertUtils.showInfo("Operacja zakończona", task.getValue());
        });

        task.setOnFailed(ev -> {
            addGraveControls.getSaveButton().setDisable(false);
            Throwable ex = task.getException();
            if (ex instanceof IllegalArgumentException) {
                AlertUtils.showError("Błędne dane", ex.getMessage());
            } else {
                AlertUtils.showError("Błąd", "Nie udało się zapisać.\nSzczegóły: " +
                        (ex != null ? ex.getMessage() : "nieznany błąd"));
            }
        });

        Thread t = new Thread(task, "save-grave");
        t.setDaemon(true);
        t.start();
    }

    private static KsiegaGrobow buildGrave(AddGraveControls controls) {
        String region = NormalizerText.normalizeGraveInput(controls.getRegionField().getEditor().getText());
        String quarter = NormalizerText.normalizeGraveInput(controls.getQuarterField().getEditor().getText());
        String row = NormalizerText.normalizeGraveInput(controls.getRowField().getEditor().getText());
        String place = NormalizerText.normalizeGraveInput(controls.getPlaceField().getEditor().getText());
        final String cemetery = AppContext.getSelectedCemetery();

        String graveIdCode = GenerateCodeFromGrave.generateFullCode(cemetery, region, quarter, row, place);

        KsiegaGrobow g = new KsiegaGrobow();
        g.setCmentarz(cemetery);
        g.setRejon(region);
        g.setKwatera(quarter);
        g.setRzad(row);
        g.setNumerMiejsca(place);
        g.setGraveIdCode(graveIdCode);
        g.setAddType(GraveAddType.MANUAL);
        return g;
    }

}
