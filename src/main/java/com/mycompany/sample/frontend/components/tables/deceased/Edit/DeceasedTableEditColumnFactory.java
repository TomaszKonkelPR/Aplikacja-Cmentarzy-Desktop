package com.mycompany.sample.frontend.components.tables.deceased.Edit;

import static com.mycompany.sample.frontend.components.tables.deceased.Edit.DeceasedEditColumnDefinitions.getErrorMessage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.components.tables.tableUtil.ColumnMeta;
import com.mycompany.sample.frontend.components.tables.tableUtil.TableUtils;
import com.mycompany.sample.frontend.controls.MainView.MainControls;
import com.mycompany.sample.frontend.view.EditDeceasedLocationView;
import com.mycompany.sample.frontend.view.ExtendPayDeceasedView;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DefaultStringConverter;

public class DeceasedTableEditColumnFactory {
    public static TableColumn<KsiegaZmarlych, Number> createIdColumn() {
        TableColumn<KsiegaZmarlych, Number> idColumn = new TableColumn<>("ID");
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

    public static TableColumn<KsiegaZmarlych, Void> createEditLocationColumn(MainControls controls) {

        TableColumn<KsiegaZmarlych, Void> col = new TableColumn<>("Akcje");
        col.setSortable(false);
        col.setPrefWidth(250);

        col.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edytuj lokalizacje");
            private final Button extendButton = new Button("Przedłuż opłate");
            private final HBox container = new HBox(5, editButton, extendButton);

            {
                editButton.getStyleClass().add("edit-location-button");
                extendButton.getStyleClass().add("extend-pay-button");

                editButton.setOnAction(e -> {
                    KsiegaZmarlych deceased = getTableView().getItems().get(getIndex());
                    if (deceased != null) {
                        EditDeceasedLocationView.showEditDeceasedView(deceased, controls);
                    }
                });

                extendButton.setOnAction(e -> {
                    KsiegaZmarlych deceased = getTableView().getItems().get(getIndex());
                    if (deceased != null) {
                        ExtendPayDeceasedView.showExtendPayDeceasedView(deceased, controls);
                    }
                });

                container.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });

        return col;
    }

    public static TableColumn<KsiegaZmarlych, String> createEditableStringColumn(
            ColumnMeta<KsiegaZmarlych> meta,
            Map<KsiegaZmarlych, List<String>> changedItems,
            VBox changesContainer,
            Button saveButton,
            ScrollPane scrollPane) {

        TableColumn<KsiegaZmarlych, String> col = new TableColumn<>(meta.getTitle());
        col.setSortable(true);

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
            String oldVal = (String) meta.getGetter().apply(row);
            String newVal = e.getNewValue();

            String error = getErrorMessage(meta.getValidator(), newVal);
            if (error != null) {
                BiConsumer<KsiegaZmarlych, String> setter = (BiConsumer<KsiegaZmarlych, String>) meta.getSetter();
                setter.accept(row, oldVal);
                return;
            }
            applyChange(meta, row, oldVal, newVal, changedItems, changesContainer, scrollPane, saveButton);
        });

        return col;
    }

    public static TableColumn<KsiegaZmarlych, LocalDate> createEditableDateColumn(
            ColumnMeta<KsiegaZmarlych> meta,
            Map<KsiegaZmarlych, List<String>> changedItems,
            VBox changesContainer,
            Button saveButton,
            ScrollPane scrollPane) {

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
                            return null;
                        }
                        try {
                            return LocalDate.parse(text.trim(), TableUtils.DATE_FORMATTER);
                        } catch (DateTimeParseException ex) {
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

                    LocalDate oldVal = (LocalDate) meta.getGetter().apply(row);
                    LocalDate newVal = dp.getValue();

                    String err = getErrorMessage(meta.getValidator(), newVal);

                    if (err != null) {
                        dp.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
                        dp.setTooltip(new Tooltip(err));
                        dp.setValue(oldVal);
                        return;
                    }

                    dp.setStyle(null);
                    dp.setTooltip(new Tooltip("Format daty: yyyy-MM-dd"));
                    applyChange(meta, row, oldVal, newVal, changedItems, changesContainer, scrollPane, saveButton);
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

    public static TableColumn<KsiegaZmarlych, Boolean> createEditableBooleanColumn(
            ColumnMeta<KsiegaZmarlych> meta,
            Map<KsiegaZmarlych, List<String>> changedItems,
            VBox changesContainer,
            Button saveButton,
            ScrollPane scrollPane) {

        TableColumn<KsiegaZmarlych, Boolean> col = new TableColumn<>(meta.getTitle());
        col.setSortable(true);

        col.setCellValueFactory(cd -> new SimpleObjectProperty<>((Boolean) meta.getGetter().apply(cd.getValue())));
        col.setCellFactory(tc -> new TableCell<>() {
            private final CheckBox cb = new CheckBox();
            {
                cb.setOnAction(ev -> {
                    KsiegaZmarlych row = getTableRow() == null ? null : getTableRow().getItem();
                    if (row == null)
                        return;
                    Boolean oldVal = (Boolean) meta.getGetter().apply(row);
                    Boolean newVal = cb.isSelected();
                    applyChange(meta, row, oldVal, newVal, changedItems, changesContainer, scrollPane, saveButton);
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

    public static <T> TableColumn<KsiegaZmarlych, T> createReadOnlyColumn(ColumnMeta<KsiegaZmarlych> meta) {
        TableColumn<KsiegaZmarlych, T> col = new TableColumn<>(meta.getTitle());
        col.setSortable(true);
        col.setCellValueFactory(cd -> new SimpleObjectProperty<>((T) meta.getGetter().apply(cd.getValue())));

        String title = meta.getTitle();
        if (title.equals("Kwatera") || title.equals("Rzad") || title.equals("Numer miejsca")) {
            TableColumn<KsiegaZmarlych, String> stringCol = (TableColumn<KsiegaZmarlych, String>) (TableColumn<?, ?>) (Object) col;

            stringCol.setComparator((a, b) -> {
                if (a == null)
                    return (b == null) ? 0 : -1;
                if (b == null)
                    return 1;

                String sa = a.trim();
                String sb = b.trim();

                int i = 0;
                while (i < sa.length() && Character.isDigit(sa.charAt(i)))
                    i++;
                int j = 0;
                while (j < sb.length() && Character.isDigit(sb.charAt(j)))
                    j++;

                boolean hasNumA = i > 0;
                boolean hasNumB = j > 0;

                if (hasNumA && !hasNumB)
                    return -1;
                if (!hasNumA && hasNumB)
                    return 1;
                if (!hasNumA && !hasNumB)
                    return sa.compareToIgnoreCase(sb);

                int numA = Integer.parseInt(sa.substring(0, i));
                int numB = Integer.parseInt(sb.substring(0, j));
                if (numA != numB)
                    return Integer.compare(numA, numB);

                String restA = sa.substring(i).trim();
                String restB = sb.substring(j).trim();

                boolean lettersA = restA.matches("^[A-Za-zĄąĆćĘęŁłŃńÓóŚśŹźŻż]+$");
                boolean lettersB = restB.matches("^[A-Za-zĄąĆćĘęŁłŃńÓóŚśŹźŻż]+$");

                if (restA.isEmpty() && !restB.isEmpty())
                    return -1;
                if (!restA.isEmpty() && restB.isEmpty())
                    return 1;

                if (lettersA && !lettersB)
                    return -1;
                if (!lettersA && lettersB)
                    return 1;

                return restA.compareToIgnoreCase(restB);
            });
        }

        return col;
    }

    private static <V> void applyChange(
            ColumnMeta<KsiegaZmarlych> meta,
            KsiegaZmarlych row,
            V oldVal,
            V newVal,
            Map<KsiegaZmarlych, List<String>> changedItems,
            VBox changesContainer,
            ScrollPane scrollPane,
            Button saveButton) {

        if (Objects.equals(oldVal, newVal))
            return;

        BiConsumer<KsiegaZmarlych, V> setter = (BiConsumer<KsiegaZmarlych, V>) meta.getSetter();
        setter.accept(row, newVal);

        addChangeRecordSimple(
                row, meta.getTitle(),
                oldVal == null ? "null" : oldVal.toString(),
                newVal == null ? "null" : newVal.toString(),
                changedItems);
        updateChangesPanel(changedItems, changesContainer, scrollPane, saveButton);
    }

    private static void addChangeRecordSimple(
            KsiegaZmarlych kz,
            String property,
            String oldValue,
            String newValue,
            Map<KsiegaZmarlych, List<String>> changedItems) {

        if (kz == null || kz.getId() == null || changedItems == null)
            return;
        List<String> changesForItem = changedItems.computeIfAbsent(kz, k -> new ArrayList<>());
        changesForItem.removeIf(s -> s.startsWith(property + ":"));
        changesForItem.add(property + ": '" + oldValue + "' → '" + newValue + "'");
    }

    private static void updateChangesPanel(
            Map<KsiegaZmarlych, List<String>> changedItems,
            VBox changesContainer,
            ScrollPane scrollPane,
            Button saveButton) {

        if (changesContainer == null || changedItems == null)
            return;

        changesContainer.getChildren().clear();
        boolean empty = changedItems.isEmpty();

        if (scrollPane != null) {
            scrollPane.setVisible(!empty);
            scrollPane.setManaged(!empty);
        }
        if (saveButton != null) {
            saveButton.setVisible(!empty);
            saveButton.setDisable(empty);
        }
        if (empty)
            return;

        for (var entry : changedItems.entrySet()) {
            KsiegaZmarlych kz = entry.getKey();
            List<String> changes = entry.getValue();

            VBox card = new VBox();
            card.getStyleClass().add("change-card");

            String name = (kz.getImie() == null ? "" : kz.getImie()) + " "
                    + (kz.getNazwisko() == null ? "" : kz.getNazwisko());
            Label personLabel = new Label(name.trim());
            personLabel.getStyleClass().add("change-card-name");
            card.getChildren().add(personLabel);

            for (String change : changes) {
                Label lbl = new Label("• " + change);
                lbl.getStyleClass().add("change-item");
                card.getChildren().add(lbl);
            }
            changesContainer.getChildren().add(card);
        }
    }
}
