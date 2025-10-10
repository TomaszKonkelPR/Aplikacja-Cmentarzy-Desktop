package com.mycompany.sample.frontend.components.tables.deceased.Add;

import static com.mycompany.sample.frontend.components.tables.deceased.Add.DeceasedAddColumnDefinitions.COLUMNS_KSIEGA_ZMARLYCH;

import java.util.List;

import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.components.tables.deceased.Edit.DeceasedTableEditColumnFactory;
import com.mycompany.sample.frontend.components.tables.tableUtil.ColumnMeta;
import com.mycompany.sample.frontend.controls.AddDeceasedControls;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class DeceasedTableAdd {
    public static TableView<KsiegaZmarlych> createDeceasedAddTable(AddDeceasedControls addDeceasedControls) {

        TableView<KsiegaZmarlych> table = new TableView<>();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setEditable(true);
        table.setItems(addDeceasedControls.getItems());

        table.getColumns().add(DeceasedTableEditColumnFactory.createIdColumn());

        buildColumns(table, COLUMNS_KSIEGA_ZMARLYCH, addDeceasedControls);
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
            AddDeceasedControls addDeceasedControls) {

        for (ColumnMeta<KsiegaZmarlych> meta : columns) {
            TableColumn<KsiegaZmarlych, ?> col = switch (meta.getType()) {
                case STRING -> DeceasedTableAddColumnFactory.createEditableStringColumn(meta, addDeceasedControls);
                case DATE -> DeceasedTableAddColumnFactory.createEditableDateColumn(meta, addDeceasedControls);
                case BOOLEAN -> DeceasedTableAddColumnFactory.createEditableBooleanColumn(meta, addDeceasedControls);
                default -> throw new IllegalArgumentException("Unexpected value: " + meta.getType());
            };
            
            table.getColumns().add(col);
        }
    }
}
