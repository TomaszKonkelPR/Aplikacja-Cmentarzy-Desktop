package com.mycompany.sample.frontend.components.tables.tableUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class TableUtils {
    public static void resizeColumnsToContent(TableView<?> table) {
        final int SAMPLE = Math.min(200, table.getItems().size());
        final double MIN = 80, MAX = 500, PADDING = 60;

        List<String> skipColumns = List.of("Akcje", "Rodzaj grobu", "Wpis do rejestru zabytków");

        for (TableColumn<?, ?> column : table.getColumns()) {
            if (skipColumns.contains(column.getText())) continue;

            double max = computeTextWidth(column.getText());

            for (int i = 0; i < SAMPLE; i++) {
                Object cellData = column.getCellData(i);
                if (cellData == null)
                    continue;

                String text = (cellData instanceof LocalDate ld)
                        ? DATE_FORMATTER.format(ld)
                        : cellData.toString();

                max = Math.max(max, computeTextWidth(text));
            }

            column.setPrefWidth(Math.max(MIN, Math.min(MAX, max + PADDING)));
        }
    }

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static double computeTextWidth(String text) {
        if (text == null || text.isEmpty())
            return 0;
        Text helper = new Text(text);
        helper.setFont(javafx.scene.text.Font.getDefault());
        return helper.getLayoutBounds().getWidth();
    }
}
