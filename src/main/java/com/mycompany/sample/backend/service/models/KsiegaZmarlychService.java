package com.mycompany.sample.backend.service.models;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitorjbl.xlsx.StreamingReader;
import com.mycompany.sample.backend.generator.GenerateCodeFromGrave;
import com.mycompany.sample.backend.mapper.MapRowToZmarly;
import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.backend.models.KsiegaZmarlych;
import com.mycompany.sample.backend.repository.KsiegaZmarlychRepository;

import jakarta.persistence.EntityManagerFactory;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class KsiegaZmarlychService {
    private final KsiegaGrobowService graveService;
    private final KsiegaZmarlychRepository deceasedBookRepo;
    private static final Logger log = LoggerFactory.getLogger("DeceasedService");

    public KsiegaZmarlychService(KsiegaGrobowService graveService, EntityManagerFactory emf) {
        this.graveService = graveService;
        this.deceasedBookRepo = new KsiegaZmarlychRepository(emf);
    }

    public void importData(Path excelPath, Label statusLabel) throws IOException {
        final int BATCH_SIZE = 500;
        final int LOG_INTERVAL = 1000;

        Map<String, Long> grobyMap = graveService.getMapOfGraves();

        int rowIndex = 0;

        List<KsiegaZmarlych> batch = new ArrayList<>(BATCH_SIZE);

        try (FileInputStream fis = new FileInputStream(excelPath.toFile());
                Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(500)
                        .bufferSize(8192)
                        .open(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                rowIndex++;

                if (rowIndex == 1) {
                    continue;
                }

                String kod = MapRowToZmarly.getCellValue(row.getCell(1)).toUpperCase();
                try {
                    KsiegaZmarlych wpis = MapRowToZmarly.map(row);
                    wpis.setGraveIdCode(kod);
                    batch.add(wpis);
                } catch (Exception ex) {
                    log.error("Błąd w wierszu " + rowIndex + ": " + ex.getMessage());
                }

                if (batch.size() >= BATCH_SIZE) {
                    deceasedBookRepo.saveAllWithGraveRefs(batch, grobyMap);
                    batch.clear();
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
                deceasedBookRepo.saveAllWithGraveRefs(batch, grobyMap);
                batch.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.info("Błąd importu: " + e.getMessage());
            Platform.runLater(() -> statusLabel.setText("Błąd importu: " + e.getMessage()));
        }
    }

    public void saveAllForSingleGrave(List<KsiegaZmarlych> deceased, KsiegaGrobow grave) {
        if (deceased.stream().anyMatch(d -> d == null || d.getId() != null)) 
            throw new IllegalArgumentException("Lista zawiera null lub encję z ID");
        deceasedBookRepo.saveAllForSingleGrave(deceased, grave);
    }

    public void saveCloseGrave(KsiegaGrobow grave, LocalDate closeDate) {
        if (grave == null)
            throw new IllegalArgumentException("Trzeba przekazać dane grobu");

        KsiegaZmarlych liquidation = new KsiegaZmarlych();
        liquidation.setImie("LIKWIDACJA");
        liquidation.setNazwisko("GROBU");
        liquidation.setDataPochowania(closeDate);
        liquidation.setGrave(grave);
        List<KsiegaZmarlych> liquidationEntry= List.of(liquidation);
        deceasedBookRepo.saveAllForSingleGrave(liquidationEntry, grave);
    }

    public long countListOfDeceasedByPattern(
            String cemeteryName, String region, String quarter,String firstname, String surname) {
        String pattern = GenerateCodeFromGrave.generatePartialCode(cemeteryName, region, quarter);
        return deceasedBookRepo.countListOfDeceasedByPattern(pattern, firstname.toLowerCase(), surname.toLowerCase());
    }

    public List<KsiegaZmarlych> getSortedListOfDeceasedByCemeteryAndQuarter(
            String cemeteryName, String region, String quarter, String firstname, String surname, int pageIndex, int pageSize) {
        String pattern = GenerateCodeFromGrave.generatePartialCode(cemeteryName, region, quarter);
        return deceasedBookRepo.getListOfDeceasedByPattern(pattern, pageIndex, pageSize, firstname.toLowerCase(), surname.toLowerCase());
    }
    

    public long countListOfDeceasedWithoutGrave(String firstname, String surname) {
        return deceasedBookRepo.countListOfDeceasedWithoutGrave(firstname.toLowerCase(),surname.toLowerCase());
    }

    public List<KsiegaZmarlych> getSortedListOfDeceasedWithoutGrave(int pageIndex, int pageSize, String firstname, String surname) {
        return deceasedBookRepo.getListOfDeceasedWithoutGrave(pageIndex, pageSize,firstname.toLowerCase(), surname.toLowerCase());
    }

    public List<KsiegaZmarlych> getListOfDeceasedByNameAndSurname(String firstname, String surname) {
        return deceasedBookRepo.getListOfDeceasedByNameAndSurname(firstname.toLowerCase(), surname.toLowerCase());
    }

    public void extendPayForGrave(Long deceasedId, int years) {
        deceasedBookRepo.extendPayForGrave(deceasedId, years);
    }

    public void setNewPayDate(Long deceasedId, LocalDate newDate) {
        deceasedBookRepo.setNewPayDate(deceasedId, newDate);
    }

    public void updateAll(List<KsiegaZmarlych> deceased) {
        if (deceased == null || deceased.isEmpty())
            throw new IllegalArgumentException("Trzeba przekazać dane zmarłych do aktualizacji");

        if (deceased.stream().anyMatch(d -> d == null || d.getId() == null)) 
            throw new IllegalArgumentException("Lista zawiera null lub encję bez ID");

        deceasedBookRepo.updateAll(deceased);
    }

    public void updateCode(Long deceasedId, String newCode) {
        if (deceasedId == null || deceasedId <= 0) {
            throw new IllegalArgumentException("Brak Id zmarłego albo nie jest liczbą dodatnią");
        }
        if (newCode == null || newCode.isBlank()) {
            throw new IllegalArgumentException("Kod grobu nie może być pusty");
        }

        deceasedBookRepo.updateDeceasedWithNewCode(deceasedId, newCode);
    }

    public long countDeceased() {
        return deceasedBookRepo.count();
    }

}