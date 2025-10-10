package com.mycompany.sample.frontend.components.tables.graves;

import static com.mycompany.sample.frontend.components.tables.graves.GravesColumnDefinitions.COLUMNS_KSIEGA_GROBOW;

import java.util.List;

import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.frontend.components.tables.tableUtil.ColumnMeta;
import com.mycompany.sample.frontend.controls.MainView.MainControls;

import javafx.collections.transformation.SortedList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class GravesTableView {

    public static TableView<KsiegaGrobow> createGravesTable(SortedList<KsiegaGrobow> sortedData, MainControls controls) {

        TableView<KsiegaGrobow> table = controls.getTables().getGravesTable();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setEditable(false);
        table.setItems(sortedData);

        table.getColumns().clear();
        table.getColumns().add(GraveTableColumnFactory.createIdColumn());
        table.getColumns().add(GraveTableColumnFactory.createAddDeceasedColumn(controls));

        buildColumns(table, COLUMNS_KSIEGA_GROBOW, controls);
        table.setPlaceholder(new Label("Brak danych"));

        return table;
    }

    public static void buildColumns(TableView<KsiegaGrobow> table, List<ColumnMeta<KsiegaGrobow>> columns, MainControls controls) {

        for (ColumnMeta<KsiegaGrobow> meta : COLUMNS_KSIEGA_GROBOW) {
            TableColumn<KsiegaGrobow, ?> col = switch (meta.getType()) {
                case DATE -> GraveTableColumnFactory.createEditableDateColumn(meta, controls);
                case GRAVETYPE -> GraveTableColumnFactory.createEditableGraveTypeColumn(meta, controls);
                case READ -> GraveTableColumnFactory.createReadOnlyColumn(meta);
                default -> throw new IllegalArgumentException("Unexpected value: " + meta.getType());
            };
            table.getColumns().add(col);
        }
    }
}
