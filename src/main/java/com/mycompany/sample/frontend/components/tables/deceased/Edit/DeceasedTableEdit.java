package com.mycompany.sample.frontend.components.tables.deceased.Edit;

import static com.mycompany.sample.frontend.components.tables.deceased.Edit.DeceasedEditColumnDefinitions.COLUMNS_KSIEGA_ZMARLYCH;

import java.util.List;
import java.util.Map;

import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.components.tables.tableUtil.ColumnMeta;
import com.mycompany.sample.frontend.controls.MainView.MainControls;

import javafx.collections.transformation.SortedList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class DeceasedTableEdit {
    public static TableView<KsiegaZmarlych> createDeceasedEditTable(SortedList<KsiegaZmarlych> sortedData,
            MainControls controls) {

        TableView<KsiegaZmarlych> table = controls.getTables().getDeceasedTable();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setEditable(true);
        table.setItems(sortedData);

        table.getColumns().add(DeceasedTableEditColumnFactory.createIdColumn());
        table.getColumns()
                .add(DeceasedTableEditColumnFactory.createEditLocationColumn(controls));

        buildColumns(table, COLUMNS_KSIEGA_ZMARLYCH, controls);
        table.setPlaceholder(new Label("Brak danych"));

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(KsiegaZmarlych deceased, boolean empty) {
                super.updateItem(deceased, empty);
                getStyleClass().remove("missing-grave-row");
                if (!empty && deceased != null) {
                    KsiegaGrobow grave = deceased.getGrave();
                    if (grave == null)
                        getStyleClass().add("missing-grave-row");
                }
            }
        });

        return table;
    }

    public static void buildColumns(
            TableView<KsiegaZmarlych> table,
            List<ColumnMeta<KsiegaZmarlych>> columns,
            MainControls controls) {

        Map<KsiegaZmarlych, List<String>> changed = controls.getTables().getChangedDeceasedItems();
        VBox changesContainer = controls.getTables().getChangesContainer();
        Button save = controls.getButtons().getSaveButton();
        ScrollPane scroll = controls.getTables().getScrollPane();

        for (ColumnMeta<KsiegaZmarlych> meta : columns) {
            TableColumn<KsiegaZmarlych, ?> col = switch (meta.getType()) {
                case STRING -> DeceasedTableEditColumnFactory.createEditableStringColumn(meta, changed,
                        changesContainer, save, scroll);
                case DATE -> DeceasedTableEditColumnFactory.createEditableDateColumn(meta, changed, changesContainer,
                        save, scroll);
                case BOOLEAN -> DeceasedTableEditColumnFactory.createEditableBooleanColumn(meta, changed,
                        changesContainer, save, scroll);
                case READ -> DeceasedTableEditColumnFactory.createReadOnlyColumn(meta);
                default -> throw new IllegalArgumentException("Unexpected value: " + meta.getType());
            };
            table.getColumns().add(col);
        }
    }

}
