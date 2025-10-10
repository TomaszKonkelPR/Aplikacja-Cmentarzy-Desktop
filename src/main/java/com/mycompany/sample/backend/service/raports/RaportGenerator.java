package com.mycompany.sample.backend.service.raports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.sample.backend.context.AppContext;
import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.backend.models.KsiegaZmarlych;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

public class RaportGenerator {

    private static final String REPORT_TEMPLATE = "/reports/raport.jrxml";
    private static final String REPORT_TITLE = "Księga grobów – " + AppContext.getSelectedCemeteryDisplayName() + " w Gdańsku";
    private static final int EMPTY_RECORDS_FILL = 10;
    private static final Logger log = LoggerFactory.getLogger("Generator");

    public static void generateReport(List<KsiegaGrobow> graves, String region, String quarter) throws JRException {
        List<KsiegaGrobow> reportData = prepareReportData(graves);
        JRSwapFileVirtualizer virtualizer = null;

        try {
            JasperReport jasperReport = JasperCache.getCompiledReport(REPORT_TEMPLATE, "ksiega_grobow");

            Map<String, Object> params = createReportParams();
            virtualizer = createVirtualizer();
            params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);
            String reportPath = SaveReportPathResolve.getReportFilePath(AppContext.getSelectedCemeteryDisplayName(),
                    region, quarter);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, reportPath);

            log.info("Wygenerowano raport: " + reportPath);
            openReportFileInFolder(reportPath);

        } catch (IOException e) {
            throw new RuntimeException("Błąd dostępu do cache/szablonu raportu", e);
        } finally {
            if (virtualizer != null)
                virtualizer.cleanup();
        }
    }

    private static Map<String, Object> createReportParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("TytulRaportu", REPORT_TITLE);
        return params;
    }

    private static JRSwapFileVirtualizer createVirtualizer() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        JRSwapFile swapFile = new JRSwapFile(tempDir.getAbsolutePath(), 1024, 100);
        return new JRSwapFileVirtualizer(2, swapFile, true);
    }

    private static List<KsiegaGrobow> prepareReportData(List<KsiegaGrobow> graves) {
        for (KsiegaGrobow gr : graves) {
            List<KsiegaZmarlych> zmarli = new ArrayList<>(gr.getPochowani());

            while (zmarli.size() < EMPTY_RECORDS_FILL) {
                zmarli.add(createEmptyDeceased());
            }
            gr.setPochowani(zmarli);
        }
        return graves;
    }

    private static KsiegaZmarlych createEmptyDeceased() {
        KsiegaZmarlych pusty = new KsiegaZmarlych();
        pusty.setNumerEwidencyjny("");
        pusty.setRodzajGrobu("");
        pusty.setPochowanyW("");
        pusty.setImie("");
        pusty.setNazwisko("");
        pusty.setAdnotacja("");
        return pusty;
    }

    public static void openReportFileInFolder(String filePath) {
        try {
            File file = new File(filePath).getAbsoluteFile();
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[] { "explorer.exe", "/select,", file.getAbsolutePath() });
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[] { "open", file.getParentFile().getAbsolutePath() });
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                Runtime.getRuntime().exec(new String[] { "xdg-open", file.getParentFile().getAbsolutePath() });
            } else {
                log.info("Nieobsługiwany system: " + os);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
