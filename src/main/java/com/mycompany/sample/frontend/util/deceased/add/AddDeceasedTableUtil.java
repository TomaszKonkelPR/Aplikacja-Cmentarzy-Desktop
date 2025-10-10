package com.mycompany.sample.frontend.util.deceased.add;

import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.frontend.components.tables.deceased.Add.DeceasedAddColumnDefinitions;
import com.mycompany.sample.frontend.controls.AddDeceasedControls;
import com.mycompany.sample.frontend.util.AlertUtils;

public class AddDeceasedTableUtil {
    public static void endInlineEdit(AddDeceasedControls addDeceasedControls) {
        if (addDeceasedControls.getTable().getEditingCell() != null) {
            addDeceasedControls.getTable().edit(-1, null);
        }
    }

    public static void addNewRow(AddDeceasedControls addDeceasedControls) {
        KsiegaZmarlych nowy = new KsiegaZmarlych();
        nowy.setGrave(addDeceasedControls.getGrave());
        addDeceasedControls.getItems().add(nowy);
        addDeceasedControls.getTable().getSelectionModel().select(nowy);
        addDeceasedControls.getTable().scrollTo(nowy);
        addDeceasedControls.getTable().refresh();
        refreshButtonsState(addDeceasedControls);
    }

    public static boolean deleteLastRow(AddDeceasedControls addDeceasedControls) {
        var items = addDeceasedControls.getItems();
        if (items.size() <= 1) {
            return false;
        }

        items.remove(items.size() - 1);

        recomputeFieldValidMapFromRows(addDeceasedControls);
        refreshButtonsState(addDeceasedControls);

        if (!items.isEmpty()) {
            var newLast = items.get(items.size() - 1);
            addDeceasedControls.getTable().getSelectionModel().select(newLast);
            addDeceasedControls.getTable().scrollTo(newLast);
        }
        return true;
    }

    public static void refreshButtonsState(AddDeceasedControls c) {
        boolean hasItems = !c.getItems().isEmpty();
        int size = c.getItems().size();
        c.getSaveButton().setDisable(!hasItems);
        c.getDeleteRowButton().setDisable(size <= 1);
    }

    public static boolean validateOrAlert(AddDeceasedControls addDeceasedControls, String errorTitle) {
        recomputeFieldValidMapFromRows(addDeceasedControls);

        if (addDeceasedControls.areAllFieldsValid()) {
            return true;
        }

        String details = collectValidationErrors(addDeceasedControls);
        AlertUtils.showError(errorTitle, details.isEmpty() ? "Uzupełnij wymagane pola." : details);
        return false;
    }

    public static void recomputeFieldValidMapFromRows(AddDeceasedControls addDeceasedControls) {
        addDeceasedControls.initFieldValidMap(DeceasedAddColumnDefinitions.COLUMNS_KSIEGA_ZMARLYCH);
        DeceasedAddColumnDefinitions.COLUMNS_KSIEGA_ZMARLYCH.forEach(col -> {
            boolean allOkForColumn = addDeceasedControls.getItems().stream().allMatch(row -> {
                Object val = col.getGetter().apply(row);
                return DeceasedAddColumnDefinitions.getErrorMessage(col.getValidator(), val) == null;
            });
            addDeceasedControls.setFieldValid(col.getTitle(), allOkForColumn);
        });
    }

    private static String collectValidationErrors(AddDeceasedControls addDeceasedControls) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < addDeceasedControls.getItems().size(); i++) {
            final int rowNo = i + 1;
            KsiegaZmarlych row = addDeceasedControls.getItems().get(i);

            DeceasedAddColumnDefinitions.COLUMNS_KSIEGA_ZMARLYCH.forEach(col -> {
                Object val = col.getGetter().apply(row);
                String err = DeceasedAddColumnDefinitions.getErrorMessage(col.getValidator(), val);
                if (err != null) {
                    sb.append("Wiersz ").append(rowNo).append(": ")
                            .append(col.getTitle()).append(" → ").append(err).append("\n");
                }
            });
        }
        return sb.toString().trim();
    }
}
