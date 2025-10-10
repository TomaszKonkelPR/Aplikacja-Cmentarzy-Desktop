package com.mycompany.sample.backend.service.raports;

import java.io.File;

public class SaveReportPathResolve {
    public static String getReportFilePath(String cemeteryName, String region, String quarter) {
        File documentsDir = new File(System.getProperty("user.home"), "Documents");
        File root = new File(documentsDir, "RaportyCmentarzy");

        if (!root.exists() && !root.mkdirs()) {
            throw new RuntimeException("Nie można utworzyć folderu bazowego: " + root.getAbsolutePath());
        }

        File cemeteryDir = new File(root, sanitize(cemeteryName));

        String r = safeTrim(region);
        String q = safeTrim(quarter);

        final String combinedFolderName;
        if (r.isEmpty()) {
            combinedFolderName = q;
        } else if (r.toUpperCase(java.util.Locale.ROOT).startsWith("KOL")) {
            combinedFolderName = r + " " + q;
        } else {
            combinedFolderName = q + " " + r;
        }

        File targetDir = new File(cemeteryDir, sanitize(combinedFolderName.trim()));
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new RuntimeException("Nie można utworzyć folderu: " + targetDir.getAbsolutePath());
        }

        String date = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fileName = "raport_ksiega_grobow_" + date + ".pdf";
        return new File(targetDir, fileName).getAbsolutePath();
    }

    private static String sanitize(String s) {
        if (s == null || s.trim().isEmpty())
            return "Brak";
        return s.replaceAll("[\\\\/:*?\"<>|]+", "_").trim();
    }

    private static String safeTrim(String s) {
        return (s == null) ? "" : s.trim();
    }

}
