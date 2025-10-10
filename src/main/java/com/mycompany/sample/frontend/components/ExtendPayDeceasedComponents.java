package com.mycompany.sample.frontend.components;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.mycompany.sample.frontend.controls.ExtendPayDeceasedControls;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.util.loader.DeceasedDataLoader;

import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.util.StringConverter;

public class ExtendPayDeceasedComponents {
    public static void setupYearCheckboxes(ExtendPayDeceasedControls extendPayDeceasedControls) {
        CheckBox[] checkboxes = { extendPayDeceasedControls.getR10(), extendPayDeceasedControls.getR20() };

        for (CheckBox cb : checkboxes) {
            cb.setOnAction(event -> {
                if (cb.isSelected()) {
                    for (CheckBox other : checkboxes) {
                        if (other != cb)
                            other.setSelected(false);
                    }

                    extendPayDeceasedControls.setYears(cb == extendPayDeceasedControls.getR10() ? 10 : 20);
                    extendPayDeceasedControls.setNewDate(null);
                    extendPayDeceasedControls.getDatePicker().setValue(null);
                    extendPayDeceasedControls.getDatePicker().setDisable(true);
                } else {
                    extendPayDeceasedControls.setYears(0);
                    extendPayDeceasedControls.getDatePicker().setDisable(false);
                }
            });
        }
    }

    public static void setupNewDatePicker(ExtendPayDeceasedControls extendPayDeceasedControls) {
        DatePicker datePicker = extendPayDeceasedControls.getDatePicker();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String text) {
                if (text == null || text.isEmpty())
                    return null;
                try {
                    return LocalDate.parse(text, formatter);
                } catch (DateTimeParseException e) {
                    datePicker.setStyle("-fx-border-color: red; -fx-border-width: 2;");
                    datePicker.setTooltip(new Tooltip("Niepoprawna data, wpisz w formacie yyyy-MM-dd"));
                    return null;
                }
            }
        });

        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            extendPayDeceasedControls.setNewDate(newDate);
            extendPayDeceasedControls.setYears(0);

            boolean hasDate = newDate != null;
            extendPayDeceasedControls.getR10().setSelected(false);
            extendPayDeceasedControls.getR20().setSelected(false);
            extendPayDeceasedControls.getR10().setDisable(hasDate);
            extendPayDeceasedControls.getR20().setDisable(hasDate);

            if (newDate != null) {
                LocalDate payDate = extendPayDeceasedControls.getDeceased().getGrobOplaconyDo();
                if (payDate != null && newDate.isBefore(payDate)) {
                    datePicker.setStyle("-fx-border-color: red; -fx-border-width: 2;");
                    datePicker.setTooltip(new Tooltip(
                            "Data musi być późniejsza niż obecna data opłacenia: " + formatter.format(payDate)));
                    return;
                }
            }

            datePicker.setStyle("");
            datePicker.setTooltip(new Tooltip("Podaj nową datę w formacie yyyy-MM-dd"));
        });

        datePicker.setPromptText(extendPayDeceasedControls.getDatePicker().getPromptText());
    }

    public static void setupConfirmButton(ExtendPayDeceasedControls extendPayDeceasedControls, MainControls controls) {

        extendPayDeceasedControls.getConfirmButton().setOnAction(event -> {
            String message;
            if (extendPayDeceasedControls.getYears() > 0) {
                message = "Czy jesteś pewny, że chcesz przedłużyć miejsce o " +
                        extendPayDeceasedControls.getYears() + " lat?";
            } else if (extendPayDeceasedControls.getNewDate() != null) {
                message = "Czy jesteś pewny, że chcesz przedłużyć miejsce do daty " +
                        extendPayDeceasedControls.getNewDate() + "?";
            } else {
                AlertUtils.showError("Błąd", "Nie wybrano żadnego okresu przedłużenia ani daty.");
                return;
            }

            boolean confirmed = AlertUtils.showConfirmation("Potwierdzenie", message);
            if (!confirmed)
                return;

            if (extendPayDeceasedControls.getYears() > 0) {
                int years = extendPayDeceasedControls.getYears();
                extendPayDeceasedControls.getDeceasedService().extendPayForGrave(extendPayDeceasedControls.getDeceased().getId(), years);
            } else if (extendPayDeceasedControls.getNewDate() != null) {
                LocalDate date = extendPayDeceasedControls.getNewDate();
                extendPayDeceasedControls.getDeceasedService().setNewPayDate(extendPayDeceasedControls.getDeceased().getId(), date);
            }

            extendPayDeceasedControls.getDialog().close();
            DeceasedDataLoader.loadDeceasedPage(controls);
        });
    }

}
