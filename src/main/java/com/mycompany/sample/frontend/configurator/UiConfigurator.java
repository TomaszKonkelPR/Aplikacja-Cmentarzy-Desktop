package com.mycompany.sample.frontend.configurator;

import com.mycompany.sample.frontend.components.AddDeceasedRecordComponents;
import com.mycompany.sample.frontend.components.AddGraveComponents;
import com.mycompany.sample.frontend.components.EditDeceasedLocationComponents;
import com.mycompany.sample.frontend.components.ExtendPayDeceasedComponents;
import com.mycompany.sample.frontend.components.MenuLayoutComponents;
import com.mycompany.sample.frontend.components.MainLayout.MainButtonComponents;
import com.mycompany.sample.frontend.components.MainLayout.MainFilterComponents;
import com.mycompany.sample.frontend.components.CemeterySelectionLayoutComponents;
import com.mycompany.sample.frontend.components.tables.deceased.Add.DeceasedAddColumnDefinitions;
import com.mycompany.sample.frontend.controls.AddDeceasedControls;
import com.mycompany.sample.frontend.controls.AddGraveControls;
import com.mycompany.sample.frontend.controls.CemeterySelectionControls;
import com.mycompany.sample.frontend.controls.EditDeceasedLocationControls;
import com.mycompany.sample.frontend.controls.ExtendPayDeceasedControls;
import com.mycompany.sample.frontend.controls.MenuControls;
import com.mycompany.sample.frontend.controls.MainView.MainControls;

public class UiConfigurator {
    public static void setupCemeterySelectionUiComponents(CemeterySelectionControls cemeterySelectionControls) {
        CemeterySelectionLayoutComponents.setupCemeteryComboBox(cemeterySelectionControls);
        CemeterySelectionLayoutComponents.setupConfirmButton(cemeterySelectionControls);

    }

    public static void setupMenuUiComponents(MenuControls menuUiControls) {
        MenuLayoutComponents.setupBooksButton(menuUiControls);
        MenuLayoutComponents.setupAddGraveButton(menuUiControls);
        MenuLayoutComponents.setupBackButton(menuUiControls);
    }

    public static void setupUiComponents(MainControls controls) {
        MainButtonComponents.setupBackButton(controls);
        MainFilterComponents.setupGraveTypeComboBox(controls);
        MainFilterComponents.setupRegionComboBox(controls);
        MainFilterComponents.setupQuarterComboBox(controls);
        MainButtonComponents.setupSaveButton(controls);
        MainButtonComponents.setupGenerateReportButton(controls);
    }

    public static void setupDeceasedEditLocationComponents(EditDeceasedLocationControls editDeceasedLocationControls,
            MainControls controls) {
        EditDeceasedLocationComponents.setupChooseTypeComboBox(editDeceasedLocationControls);
        EditDeceasedLocationComponents.setupChooseRegionComboBox(editDeceasedLocationControls);
        EditDeceasedLocationComponents.setupChooseQuarterComboBox(editDeceasedLocationControls);
        EditDeceasedLocationComponents.setupChooseRowComboBox(editDeceasedLocationControls);
        EditDeceasedLocationComponents.setupChoosePlaceComboBox(editDeceasedLocationControls);
        EditDeceasedLocationComponents.setupCancelButton(editDeceasedLocationControls);
        EditDeceasedLocationComponents.setupSaveButton(editDeceasedLocationControls, controls);
    }

    public static void setupExtendPayDeceasedComponents(ExtendPayDeceasedControls extendPayDeceasedControls,
            MainControls controls) {
        ExtendPayDeceasedComponents.setupYearCheckboxes(extendPayDeceasedControls);
        ExtendPayDeceasedComponents.setupNewDatePicker(extendPayDeceasedControls);
        ExtendPayDeceasedComponents.setupConfirmButton(extendPayDeceasedControls, controls);
    }

    public static void setupAddDeceasedRecordComponents(AddDeceasedControls addDeceasedControls) {
        AddDeceasedRecordComponents.setupSearchButton(addDeceasedControls);
        addDeceasedControls.initFieldValidMap(DeceasedAddColumnDefinitions.COLUMNS_KSIEGA_ZMARLYCH);
        AddDeceasedRecordComponents.setupAddRow(addDeceasedControls);
        AddDeceasedRecordComponents.setupDeleteRowButton(addDeceasedControls);
        AddDeceasedRecordComponents.setupSaveButton(addDeceasedControls);
        AddDeceasedRecordComponents.setupCancelButton(addDeceasedControls);
        AddDeceasedRecordComponents.setupGenerateSingleRaportButton(addDeceasedControls);
        AddDeceasedRecordComponents.setupCloseDatePicker(addDeceasedControls);
        AddDeceasedRecordComponents.setupCloseGraveButton(addDeceasedControls);
    }

    public static void setupAddGraveComponents(AddGraveControls addGraveControls) {
        AddGraveComponents.setupRegionField(addGraveControls);
        AddGraveComponents.setupQuarterField(addGraveControls);
        AddGraveComponents.setupRowField(addGraveControls);
        AddGraveComponents.setupSaveButton(addGraveControls);
        AddGraveComponents.setupCancelButton(addGraveControls);

    }

}
