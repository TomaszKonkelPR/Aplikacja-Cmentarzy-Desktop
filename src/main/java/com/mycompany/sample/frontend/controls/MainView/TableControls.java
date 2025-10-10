package com.mycompany.sample.frontend.controls.MainView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.backend.models.KsiegaZmarlych;

public class TableControls {
    private final TableView<KsiegaZmarlych> deceasedTable = new TableView<>();
    private final TableView<KsiegaGrobow>   gravesTable   = new TableView<>();

    private final ObservableList<KsiegaZmarlych> deceasedData = FXCollections.observableArrayList();
    private final ObservableList<KsiegaGrobow>   gravesData   = FXCollections.observableArrayList();

    private final ProgressIndicator loadingIndicator = new ProgressIndicator();

    private final Map<KsiegaZmarlych, List<String>> changedDeceasedItems = new HashMap<>();
    private final Map<KsiegaGrobow,   List<String>> changeGravesItems    = new HashMap<>();

    private final VBox       changesContainer = new VBox(5);
    private final ScrollPane scrollPane       = new ScrollPane(changesContainer);

    public TableView<KsiegaZmarlych> getDeceasedTable() { return deceasedTable; }
    public TableView<KsiegaGrobow>   getGravesTable()   { return gravesTable; }

    public ObservableList<KsiegaZmarlych> getDeceasedData() { return deceasedData; }
    public ObservableList<KsiegaGrobow>   getGravesData()   { return gravesData; }

    public ProgressIndicator getLoadingIndicator() { return loadingIndicator; }

    public Map<KsiegaZmarlych, List<String>> getChangedDeceasedItems() { return changedDeceasedItems; }
    public Map<KsiegaGrobow,   List<String>> getChangeGravesItems()    { return changeGravesItems; }

    public VBox getChangesContainer() { return changesContainer; }
    public ScrollPane getScrollPane() { return scrollPane; }
}
