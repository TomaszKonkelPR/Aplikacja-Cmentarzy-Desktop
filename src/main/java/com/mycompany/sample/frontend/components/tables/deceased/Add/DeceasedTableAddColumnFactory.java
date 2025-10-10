package com.mycompany.sample.frontend.components.tables.deceased.Add;

import static com.mycompany.sample.frontend.components.tables.deceased.Add.DeceasedAddColumnDefinitions.getErrorMessage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.BiConsumer;

import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.components.tables.tableUtil.ColumnMeta;
import com.mycompany.sample.frontend.components.tables.tableUtil.TableUtils;
import com.mycompany.sample.frontend.controls.AddDeceasedControls;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

public class DeceasedTableAddColumnFactory {
    public static TableColumn<KsiegaZmarlych, String> createEditableStringColumn(ColumnMeta<KsiegaZmarlych> meta,
            AddDeceasedControls addDeceasedControls) {

        TableColumn<KsiegaZmarlych, String> col = new TableColumn<>(meta.getTitle());
        col.setSortable(false);

        col.setCellValueFactory(cd -> new SimpleStringProperty((String) meta.getGetter().apply(cd.getValue())));
        col.setCellFactory(tc -> new TextFieldTableCell<>(new DefaultStringConverter()) {
            @Override
            public void startEdit() {
                super.startEdit();
                if (getGraphic() instanceof TextField tf) {
                    tf.textProperty().addListener((o, oldV, newV) -> {
                        String err = getErrorMessage(meta.getValidator(), newV);
                        if (err != null) {
                            tf.setStyle("-fx-border-color: red;");
                            tf.setTooltip(new Tooltip(err));
                        } else {
                            tf.setStyle(null);
                            tf.setTooltip(null);
                        }
                    });
                }
            }
        });

        col.setOnEditCommit(e -> {
            KsiegaZmarlych row = e.getRowValue();
            String newVal = e.getNewValue();
            String err = getErrorMessage(meta.getValidator(), newVal);

            BiConsumer<KsiegaZmarlych, String> setter = (BiConsumer<KsiegaZmarlych, String>) meta.getSetter();

            if (err != null) {
                String oldVal = e.getOldValue();
                setter.accept(row, oldVal);
                addDeceasedControls.setFieldValid(meta.getTitle(), false);
            } else {
                setter.accept(row, newVal);
                addDeceasedControls.setFieldValid(meta.getTitle(), true);
            }
        });

        return col;
    }

    public static TableColumn<KsiegaZmarlych, LocalDate> createEditableDateColumn(ColumnMeta<KsiegaZmarlych> meta,
            AddDeceasedControls addDeceasedControls) {

        TableColumn<KsiegaZmarlych, LocalDate> col = new TableColumn<>(meta.getTitle());
        col.setSortable(true);

        col.setCellValueFactory(cd -> new SimpleObjectProperty<>((LocalDate) meta.getGetter().apply(cd.getValue())));
        col.setCellFactory(tc -> new TableCell<>() {
            private final DatePicker dp = new DatePicker();

            {
                dp.setEditable(true);
                dp.setTooltip(new Tooltip("Format daty: yyyy-MM-dd"));
                dp.setPromptText("yyyy-MM-dd");
                dp.setConverter(new javafx.util.StringConverter<LocalDate>() {
                    @Override
                    public String toString(LocalDate date) {
                        return date == null ? "" : TableUtils.DATE_FORMATTER.format(date);
                    }

                    @Override
                    public LocalDate fromString(String text) {
                        if (text == null || text.isBlank()) {
                            dp.setStyle(null);
                            dp.setTooltip(new Tooltip("Format daty: yyyy-MM-dd"));
                            return null;
                        }
                        try {
                            dp.setStyle(null);
                            dp.setTooltip(new Tooltip("Format daty: yyyy-MM-dd"));
                            return LocalDate.parse(text.trim(), TableUtils.DATE_FORMATTER);
                        } catch (DateTimeParseException ex) {
                            dp.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
                            dp.setTooltip(new Tooltip("Nieprawidłowy format. Poprawny: yyyy-MM-dd"));
                            return null;
                        }
                    }
                });
                dp.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (date != null && date.isAfter(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffe6e6;");
                        }
                    }
                });

                dp.setOnAction(ev -> {
                    KsiegaZmarlych row = getTableRow() == null ? null : getTableRow().getItem();
                    if (row == null)
                        return;

                    LocalDate newVal = dp.getValue();
                    String err = getErrorMessage(meta.getValidator(), newVal);
                    BiConsumer<KsiegaZmarlych, LocalDate> setter = (BiConsumer<KsiegaZmarlych, LocalDate>) meta
                            .getSetter();

                    if (err != null) {
                        dp.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
                        dp.setTooltip(new Tooltip(err));
                        addDeceasedControls.setFieldValid(meta.getTitle(), false);
                    } else {
                        setter.accept(row, newVal);
                        dp.setStyle(null);
                        dp.setTooltip(new Tooltip("Format daty: yyyy-MM-dd"));
                        addDeceasedControls.setFieldValid(meta.getTitle(), true);
                    }
                });
            }

            @Override
            protected void updateItem(LocalDate v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    dp.setValue(v);
                    dp.setStyle(null);
                    dp.setTooltip(new Tooltip("Format daty: yyyy-MM-dd"));
                    setGraphic(dp);
                }
            }
        });

        return col;
    }

    public static TableColumn<KsiegaZmarlych, Boolean> createEditableBooleanColumn(ColumnMeta<KsiegaZmarlych> meta,
            AddDeceasedControls addDeceasedControls) {

        TableColumn<KsiegaZmarlych, Boolean> col = new TableColumn<>(meta.getTitle());
        col.setSortable(false);

        col.setCellValueFactory(cd -> new SimpleObjectProperty<>((Boolean) meta.getGetter().apply(cd.getValue())));
        col.setCellFactory(tc -> new TableCell<>() {
            private final CheckBox cb = new CheckBox();
            {
                cb.setOnAction(ev -> {
                    KsiegaZmarlych row = getTableRow() == null ? null : getTableRow().getItem();
                    if (row == null)
                        return;
                    Boolean newVal = cb.isSelected();

                    BiConsumer<KsiegaZmarlych, Boolean> setter = (BiConsumer<KsiegaZmarlych, Boolean>) meta.getSetter();

                    String err = getErrorMessage(meta.getValidator(), newVal);
                    if (err != null) {
                        Boolean oldVal = (Boolean) meta.getGetter().apply(row);
                        cb.setSelected(Boolean.TRUE.equals(oldVal));
                        addDeceasedControls.setFieldValid(meta.getTitle(), false);
                    } else {
                        setter.accept(row, newVal);
                        addDeceasedControls.setFieldValid(meta.getTitle(), true);
                    }
                });
            }

            @Override
            protected void updateItem(Boolean v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty || getTableRow() == null || getTableRow().getItem() == null ? null : cb);
                if (!empty)
                    cb.setSelected(Boolean.TRUE.equals(v));
            }
        });

        return col;
    }
}
