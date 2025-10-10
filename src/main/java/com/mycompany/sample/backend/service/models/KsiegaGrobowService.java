package com.mycompany.sample.backend.service.models;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitorjbl.xlsx.StreamingReader;
import com.mycompany.sample.backend.mapper.MapRowToGrob;
import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.backend.repository.KsiegaGrobowRepository;
import com.mycompany.sample.frontend.util.text.comparator.StringNumericComparator;

import jakarta.persistence.EntityManagerFactory;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class KsiegaGrobowService {
    private final KsiegaGrobowRepository graveBookRepo;
    private static final Logger log = LoggerFactory.getLogger("GraveService");

    public KsiegaGrobowService(EntityManagerFactory emf) {
        this.graveBookRepo = new KsiegaGrobowRepository(emf);
    }

    public void importData(Path excelPath, Label statusLabel) {
        final int BATCH_SIZE = 500;
        final int LOG_INTERVAL = 1000;

        List<KsiegaGrobow> batch = new ArrayList<>(BATCH_SIZE);
        Set<String> inBatch = new HashSet<>();

        try (FileInputStream fis = new FileInputStream(excelPath.toFile());
                Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(500)
                        .bufferSize(8192)
                        .open(fis)) {

            Sheet sheet = workbook.getSheetAt(1);
            int rowIndex = 0;

            for (Row row : sheet) {
                rowIndex++;

                if (rowIndex <= 2)
                    continue;

                try {
                    KsiegaGrobow wpis = MapRowToGrob.map(row);
                    String code = wpis.getGraveIdCode();
                    if (code == null || code.isBlank()) {
                        continue;
                    }

                    String codeUpper = code.toUpperCase();
                    if (!inBatch.add(codeUpper)) {
                        continue;
                    }

                    wpis.setGraveIdCode(codeUpper);
                    batch.add(wpis);

                } catch (Exception ex) {
                    log.error("Błąd w wierszu " + rowIndex + ": " + ex.getMessage());
                }

                if (batch.size() >= BATCH_SIZE) {
                    graveBookRepo.saveAll(batch);
                    batch.clear();
                    inBatch.clear();
                }

                if (rowIndex % LOG_INTERVAL == 0) {
                    String msg = "Wczytano " + rowIndex + " wierszy...";
                    log.info(msg);
                    Platform.runLater(() -> statusLabel.setText(msg));
                }
            }

            if (!batch.isEmpty()) {
                String msg = "Wczytano ostatnie " + batch.size() + " wierszy...";
                log.info(msg);
                Platform.runLater(() -> statusLabel.setText(msg));

                graveBookRepo.saveAll(batch);
                batch.clear();
                inBatch.clear();
            }

            log.info("Import zakończony.");
            Platform.runLater(() -> statusLabel.setText("Import zakończony."));
            graveBookRepo.cleanupDuplicatesKeepMinId();

        } catch (Exception e) {
            e.printStackTrace();
            log.info("Błąd importu: " + e.getMessage());
            Platform.runLater(() -> statusLabel.setText("Błąd importu: " + e.getMessage()));
            batch.clear();
        } finally {
            if (!batch.isEmpty()) {
                try {
                    graveBookRepo.saveAll(batch);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public String saveNewGrave(KsiegaGrobow ksiegaGrobow) {
        if (ksiegaGrobow == null)
            throw new IllegalArgumentException("Encja == null");
        if (ksiegaGrobow.getId() != null)
            throw new IllegalArgumentException("saveNew przyjmuje tylko nowe encje (id == null)");
        if (ksiegaGrobow.getGraveIdCode() == null || ksiegaGrobow.getGraveIdCode().isBlank())
            throw new IllegalArgumentException("Brak graveIdCode");
        if (ksiegaGrobow.getCmentarz() == null || ksiegaGrobow.getCmentarz().isBlank())
            throw new IllegalArgumentException("Brak cmentarza");
        return graveBookRepo.saveNewGrave(ksiegaGrobow);
    }

    public long countGraves() {
        return graveBookRepo.count();
    }

    public List<KsiegaGrobow> getAll() {
        return graveBookRepo.getAll();
    }

    public KsiegaGrobow findByGraveIdCode(String code) {
        return graveBookRepo.findByGraveIdCode(code);
    }

    public KsiegaGrobow findByGraveId(Long id) {
        return graveBookRepo.findByGraveId(id);
    }

    public List<String> getListOfCemetery() {
        return graveBookRepo.getListOfCemetery();
    }

    public List<String> getListOfAllRegionsByCemetery(String cemetery) {
        List<String> regions = graveBookRepo.getListOfAllRegionsByCemetery(cemetery);
        regions.sort(StringNumericComparator.STRING_WITH_NUMERIC_SUFFIX_COMPARATOR);
        return regions;
    }

    public List<String> getListOfColumbariumByCemetery(String cemetery) {
        List<String> columbarium = graveBookRepo.getListOfColumbariumByCemetery(cemetery);
        columbarium.sort(StringNumericComparator.STRING_WITH_NUMERIC_SUFFIX_COMPARATOR);
        return columbarium;
    }

    public List<String> getListOfRegionByCemetery(String cemetery) {
        List<String> regions = graveBookRepo.getListOfRegionByCemetery(cemetery);
        regions.sort(StringNumericComparator.STRING_WITH_NUMERIC_SUFFIX_COMPARATOR);
        return regions;
    }

    public List<String> getListOfQuarterForRegionsByCemetery(String cemetery, String region) {
        List<String> quarters = graveBookRepo.getListOfQuarterForRegionsByCemetery(cemetery, region);
        quarters.sort(StringNumericComparator.NUMERIC_WITH_SUFFIX_COMPARATOR);
        return quarters;
    }

    public List<String> getListOfRowsByCemeteryAndQuarter(String cemetery, String region, String quarter) {
        List<String> rows = graveBookRepo.getListOfRowsByCemeteryAndQuarter(cemetery, region, quarter);
        rows.sort(StringNumericComparator.NUMERIC_WITH_SUFFIX_COMPARATOR);
        return rows;
    }

    public List<String> getListOfPlacesByCemeteryAndQuarterAndRow(String cemetery, String region, String quarter, String row) {
        List<String> places = graveBookRepo.getListOfPlacesByCemeteryAndQuarterAndRow(cemetery, region, quarter, row);
        places.sort(StringNumericComparator.NUMERIC_WITH_SUFFIX_COMPARATOR);
        return places;
    }

    public List<KsiegaGrobow> getGravesByCemeteryAndQuarter(String cemetery, String region, String quarter) {
        return graveBookRepo.getGravesByCemeteryAndQuarter(cemetery, region, quarter);
    }

    public Map<String, Long> getMapOfGraves() {
        List<KsiegaGrobow> allGraves = graveBookRepo.getAll();
        Map<String, Long> map = new HashMap<>();

        for (KsiegaGrobow g : allGraves) {
            if (g.getGraveIdCode() != null && g.getId() != null) {
                map.put(g.getGraveIdCode(), g.getId());
            }
        }
        return map;
    }

    public void update(Long id, LocalDate newDate) {
        graveBookRepo.updateRejestrZabytkowDate(id, newDate);
    }

    public void updateGraveType(Long id, String newType) {
        graveBookRepo.updateGraveType(id, newType);
    }

    public void updateAll(List<KsiegaGrobow> graves) throws SQLException {
        graveBookRepo.updateAll(graves);
    }

}
