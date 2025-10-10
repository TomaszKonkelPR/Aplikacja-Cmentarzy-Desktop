package com.mycompany.sample.frontend.binder;

import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.components.MainLayout.MainButtonComponents;
import com.mycompany.sample.frontend.components.tables.deceased.Add.DeceasedTableAdd;
import com.mycompany.sample.frontend.components.tables.deceased.Edit.DeceasedTableEdit;
import com.mycompany.sample.frontend.components.tables.graves.GravesTableView;
import com.mycompany.sample.frontend.components.tables.tableUtil.TableUtils;
import com.mycompany.sample.frontend.controls.AddDeceasedControls;
import com.mycompany.sample.frontend.controls.MainView.MainControls;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;

public class DataBinder {
        public static void setupMainViewData(MainControls controls) {
                FilteredList<KsiegaZmarlych> filteredDeceasedData = new FilteredList<>(controls.getTables().getDeceasedData(),p -> true);
                SortedList<KsiegaZmarlych> sortedDeceasedData = new SortedList<>(filteredDeceasedData);
                sortedDeceasedData.comparatorProperty().bind(controls.getTables().getDeceasedTable().comparatorProperty());

                FilteredList<KsiegaGrobow> filteredGravesData = new FilteredList<>(controls.getTables().getGravesData(),p -> true);
                SortedList<KsiegaGrobow> sortedGravesData = new SortedList<>(filteredGravesData);
                sortedGravesData.comparatorProperty().bind(controls.getTables().getGravesTable().comparatorProperty());

                DeceasedTableEdit.createDeceasedEditTable(sortedDeceasedData, controls);
                GravesTableView.createGravesTable(sortedGravesData, controls);

                MainButtonComponents.setupChangeViewButton(controls);

                MainButtonComponents.setupLoadButton(controls);
        }

        public static void setupAddDeceasedRecordData(AddDeceasedControls addDeceasedControls) {
                TableView<KsiegaZmarlych> table = DeceasedTableAdd.createDeceasedAddTable(addDeceasedControls);
                addDeceasedControls.setTable(table);
                TableUtils.resizeColumnsToContent(addDeceasedControls.getTable());
        }
}
