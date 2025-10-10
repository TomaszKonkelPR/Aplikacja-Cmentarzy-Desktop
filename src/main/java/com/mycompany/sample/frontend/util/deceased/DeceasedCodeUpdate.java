package com.mycompany.sample.frontend.util.deceased;

import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.controls.EditDeceasedLocationControls;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.util.loader.DeceasedDataLoader;

import javafx.concurrent.Task;
import javafx.stage.Stage;

public class DeceasedCodeUpdate {
    public static void updateGraveCode(EditDeceasedLocationControls editDeceasedLocationControls, MainControls controls) {
        Stage dialog = editDeceasedLocationControls.getDialog();
        KsiegaZmarlych deceased = editDeceasedLocationControls.getDeceased();
        String newCode = editDeceasedLocationControls.getNewCode();
        System.out.println(newCode);

        editDeceasedLocationControls.getSaveButton().setDisable(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                controls.getDeceasedService().updateCode(deceased.getId(), newCode);
                return null;
            }
        };

        task.setOnSucceeded(ev -> {
            
            AlertUtils.showInfo("Zaktualizowano", null,
                    "Zaktualizowano kod grobu na:\n" + newCode);
            if (dialog != null) dialog.close();
            DeceasedDataLoader.loadDeceasedPage(controls);
        });

        task.setOnFailed(ev -> {
            Throwable ex = task.getException();
            AlertUtils.showError("Błąd", "Nie udało się zaktualizować kodu grobu:\n" + ex.getMessage());
            editDeceasedLocationControls.getSaveButton().setDisable(false);
        });

        Thread t = new Thread(task, "update-grave-code");
        t.setDaemon(true);
        t.start();
    }
}
