package com.mycompany.sample.frontend.components.MainLayout.helper;

import java.util.Locale;

import com.mycompany.sample.frontend.controls.MainView.MainControls;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class ComboBoxHelper {

    public static void makeFilterable(ComboBox<String> cb, ObservableList<String> source) {
        FilteredList<String> filtered = new FilteredList<>(source, s -> true);
        cb.setItems(filtered);
        cb.setEditable(true);

        TextField editor = cb.getEditor();
        PauseTransition debounce = new PauseTransition(Duration.millis(250));
        BooleanProperty suspendFilter = new SimpleBooleanProperty(false);
        BooleanProperty programmatic = new SimpleBooleanProperty(false);

        editor.textProperty().addListener((obs, old, text) -> {
            if (suspendFilter.get() || programmatic.get())
                return;

            debounce.stop();
            debounce.setOnFinished(e -> Platform.runLater(() -> {
                String q = normalize(text);

                String selected = cb.getSelectionModel().getSelectedItem();

                filtered.setPredicate(item -> q.isEmpty()
                        || (item != null && normalize(item).contains(q)));

                if (selected != null && !filtered.contains(selected)) {
                    cb.getSelectionModel().clearSelection();
                    cb.setValue(null);
                }

                if (!filtered.isEmpty() && cb.isFocused() && !cb.isShowing()) {
                    cb.show();
                }
            }));
            debounce.playFromStart();
        });

        cb.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            if (sel == null)
                return;

            suspendFilter.set(true);
            programmatic.set(true);
            Platform.runLater(() -> {
                editor.setText(sel);
                editor.positionCaret(sel.length());
                filtered.setPredicate(s -> true); 
                cb.hide();
                programmatic.set(false);
                suspendFilter.set(false);
            });
        });

    }

    private static String normalize(String s) {
        return (s == null ? "" : s.trim().toLowerCase(Locale.ROOT));
    }

    public static void configureNoneValue(ComboBox<String> regionComboBox) {
        regionComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null || item.trim().isEmpty()) ? "Bez" : item);
            }
        });

        regionComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null || item.trim().isEmpty()) ? "Bez" : item);
            }
        });
    }

    public static void resetAllFilters(MainControls controls) {
        ComboBox<String> regionComboBox = controls.getFilters().getRegionComboBox();
        ComboBox<String> quarterComboBox = controls.getFilters().getQuarterComboBox();
        TextField firstnameTextField = controls.getFilters().getFirstnameTextField();
        TextField surnameTextField = controls.getFilters().getSurnameTextField();

        regionComboBox.getSelectionModel().clearSelection();
        quarterComboBox.getSelectionModel().clearSelection();

        regionComboBox.setDisable(true);
        quarterComboBox.setDisable(true);

        firstnameTextField.setDisable(true);
        firstnameTextField.clear();
        surnameTextField.setDisable(true);
        surnameTextField.clear();

        controls.getButtons().getLoadButton().setDisable(true);
        controls.getPagination().getPrevButton().setDisable(true);
        controls.getPagination().getNextButton().setDisable(true);
    }
}
