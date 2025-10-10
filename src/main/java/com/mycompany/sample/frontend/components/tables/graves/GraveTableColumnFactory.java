package com.mycompany.sample.frontend.components.tables.graves;

import static com.mycompany.sample.frontend.components.tables.graves.GravesColumnDefinitions.getErrorMessage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.BiConsumer;

import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.frontend.components.tables.tableUtil.ColumnMeta;
import com.mycompany.sample.frontend.components.tables.tableUtil.TableUtils;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.util.AlertUtils;
import com.mycompany.sample.frontend.util.graves.ChangeGraveDataUtil;
import com.mycompany.sample.frontend.view.AddDeceasedRecordView;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

public class GraveTableColumnFactory {

    public static TableColumn<KsiegaGrobow, Number> createIdColumn() {
        TableColumn<KsiegaGrobow, Number> idColumn = new TableColumn<>("ID");
        idColumn.setSortable(false);
        idColumn.setReorderable(false);
        idColumn.setPrefWidth(50);
        idColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty ? null : Integer.toString(getIndex() + 1));
            }
        });
        return idColumn;
    }

    public static TableColumn<KsiegaGrobow, Void> createAddDeceasedColumn(MainControls controls) {

        TableColumn<KsiegaGrobow, Void> col = new TableColumn<>("Wpisy");
        col.setSortable(false);
        col.setPrefWidth(120);
        col.setCellFactory(param -> new TableCell<>() {
            private final Button addButton = new Button("Szczegóły");
            {
                addButton.getStyleClass().add("edit-location-button");
                addButton.setOnAction(e -> {
                    KsiegaGrobow grave = getTableView().getItems().get(getIndex());
                    AddDeceasedRecordView.showAddDeceasedRecord(controls.getPrimaryStage(), grave, controls);
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : addButton);
            }
        });
        return col;
    }

    public static <T> TableColumn<KsiegaGrobow, T> createReadOnlyColumn(ColumnMeta<KsiegaGrobow> meta) {
        TableColumn<KsiegaGrobow, T> column = new TableColumn<>(meta.getTitle());
        column.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>((T) meta.getGetter().apply(cellData.getValue())));
        column.setSortable(true);
        return column;
    }

    public static TableColumn<KsiegaGrobow, LocalDate> createEditableDateColumn(ColumnMeta<KsiegaGrobow> meta, MainControls controls) {
        TableColumn<KsiegaGrobow, LocalDate> col = new TableColumn<>(meta.getTitle());
        col.setSortable(true);
        col.setPrefWidth(260);

        col.setCellValueFactory(cd -> new SimpleObjectProperty<>((LocalDate) meta.getGetter().apply(cd.getValue())));
        col.setCellFactory(tc -> new TableCell<>() {
            private final DatePicker dp = new DatePicker();
            private final Button saveBtn = new Button("Zmień");

            {
                saveBtn.setDisable(true);
                dp.setEditable(true);
                dp.setPromptText("yyyy-MM-dd");
                dp.setTooltip(new Tooltip("Format daty: yyyy-MM-dd"));
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
                    KsiegaGrobow row = getTableRow() == null ? null : getTableRow().getItem();
                    if (row == null)
                        return;

                    LocalDate newVal = dp.getValue();
                    BiConsumer<KsiegaGrobow, LocalDate> setter = (BiConsumer<KsiegaGrobow, LocalDate>) meta.getSetter();
                    String err = getErrorMessage(meta.getValidator(), newVal);

                    if (err != null) {
                        saveBtn.setDisable(true);
                        dp.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
                        dp.setTooltip(new Tooltip(err));
                    } else {
                        setter.accept(row, newVal);
                        saveBtn.setDisable(false);
                        dp.setStyle(null);
                        dp.setTooltip(new Tooltip("Format daty: yyyy-MM-dd"));
                    }
                });

                saveBtn.setOnAction(ev -> {
                    KsiegaGrobow row = getTableRow() == null ? null : getTableRow().getItem();
                    if (row == null)
                        return;

                    LocalDate newDate = dp.getValue();
                    if (newDate == null) {
                        boolean confirmClear = AlertUtils.showConfirmation(
                                "Usuwanie daty",
                                "Czy na pewno chcesz wyczyścić datę?");

                        if (confirmClear) {
                            ChangeGraveDataUtil.updateAntiqueDate(row, null, controls, saveBtn);
                        }
                        return;
                    }
                    String formattedDate = TableUtils.DATE_FORMATTER.format(newDate);

                    boolean confirmed = AlertUtils.showConfirmation(
                            "Potwierdzenie zmiany daty",
                            "Czy na pewno chcesz zmienić datę na: " + formattedDate + "?");

                    if (confirmed) {
                        ChangeGraveDataUtil.updateAntiqueDate(row, newDate, controls, saveBtn);
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
                    saveBtn.setDisable(true);
                    HBox box = new HBox(5, dp, saveBtn);
                    setGraphic(box);
                }
            }
        });

        return col;
    }

    public static TableColumn<KsiegaGrobow, String> createEditableGraveTypeColumn(
            ColumnMeta<KsiegaGrobow> meta,
            MainControls controls) {

        TableColumn<KsiegaGrobow, String> col = new TableColumn<>(meta.getTitle());
        col.setSortable(true);
        col.setPrefWidth(250);

        col.setCellValueFactory(cd -> new SimpleObjectProperty<>((String) meta.getGetter().apply(cd.getValue())));

        col.setCellFactory(tc -> new TableCell<>() {
            private final ComboBox<String> comboBox = new ComboBox<>();
            private final Button saveBtn = new Button("Zmień");

            {
                comboBox.getItems().addAll("ZIEMNY", "KOLUMBARIUM", "MUROWANY");
                saveBtn.setDisable(true);

                comboBox.setOnAction(ev -> {
                    KsiegaGrobow row = getTableRow() == null ? null : getTableRow().getItem();
                    if (row == null)
                        return;

                    String newVal = comboBox.getValue();
                    BiConsumer<KsiegaGrobow, String> setter = (BiConsumer<KsiegaGrobow, String>) meta.getSetter();
                    setter.accept(row, newVal);
                    saveBtn.setDisable(false);
                });

                saveBtn.setOnAction(ev -> {
                    KsiegaGrobow row = getTableRow() == null ? null : getTableRow().getItem();
                    if (row == null)
                        return;

                    String newVal = comboBox.getValue();

                    boolean confirmed = AlertUtils.showConfirmation(
                            "Potwierdzenie zmiany typu grobu",
                            "Czy na pewno chcesz zmienić typ grobu na: " + newVal + "?");

                    if (confirmed) {
                        ChangeGraveDataUtil.updateGraveType(row, newVal, controls, saveBtn);
                    }
                });
            }

            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    comboBox.setValue(val);
                    comboBox.setTooltip(null);
                    saveBtn.setDisable(true);
                    HBox box = new HBox(5, comboBox, saveBtn);
                    setGraphic(box);
                }
            }
        });

        return col;
    }

}
