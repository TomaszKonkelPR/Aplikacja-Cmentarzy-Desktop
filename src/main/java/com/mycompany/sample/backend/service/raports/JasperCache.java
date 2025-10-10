package com.mycompany.sample.backend.service.raports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class JasperCache {

    private static final String APP_CACHE_DIR = resolveCacheDir();
    private static final Logger log = LoggerFactory.getLogger("Raports");

    static JasperReport getCompiledReport(String jrxmlResourcePath, String cacheKey) throws JRException, IOException {
        byte[] jrxmlBytes;
        try (InputStream in = JasperCache.class.getResourceAsStream(jrxmlResourcePath)) {
            if (in == null)
                throw new FileNotFoundException("Brak zasobu: " + jrxmlResourcePath);
            jrxmlBytes = in.readAllBytes();
        }

        String sha = sha256Hex(jrxmlBytes);

        Path cacheDir = Path.of(APP_CACHE_DIR, "reports-cache");
        Files.createDirectories(cacheDir);
        Path jasperFile = cacheDir.resolve(cacheKey + ".jasper");
        Path shaFile = cacheDir.resolve(cacheKey + ".sha256");

        if (Files.isReadable(jasperFile) && Files.isReadable(shaFile)) {
            String savedSha = Files.readString(shaFile).trim();
            if (sha.equalsIgnoreCase(savedSha)) {
                log.info("Ładuję raport z cache: " + jasperFile);
                Object obj = JRLoader.loadObject(jasperFile.toFile());
                if (obj instanceof JasperReport jr) {
                    return jr;
                } else {
                    log.info("Plik .jasper nie jest poprawny — rekompilacja...");
                }
            } else {
                log.info("SHA zmienione — rekompilacja raportu: " + jasperFile);
            }
        } else {
            log.info("Brak pliku .jasper lub SHA — kompilacja od nowa: " + jasperFile);
        }

        JasperReport compiled = JasperCompileManager.compileReport(new ByteArrayInputStream(jrxmlBytes));
        JRSaver.saveObject(compiled, jasperFile.toFile());
        Files.writeString(shaFile, sha);
        return compiled;
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(bytes);
            StringBuilder sb = new StringBuilder(dig.length * 2);
            for (byte b : dig)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private static String resolveCacheDir() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String userHome = System.getProperty("user.home");
        if (os.contains("win")) {
            String localAppData = System.getenv("LOCALAPPDATA");
            if (localAppData != null && !localAppData.isBlank())
                return localAppData + File.separator + "RaportCache";
            return userHome + File.separator + "AppData" + File.separator + "Local" + File.separator + "RaportCache";
        } else if (os.contains("mac")) {
            return userHome + File.separator + "Library" + File.separator + "Application Support" + File.separator
                    + "RaportCache";
        } else {
            String xdg = System.getenv("XDG_CACHE_HOME");
            if (xdg != null && !xdg.isBlank())
                return xdg + File.separator + "RaportCache";
            return userHome + File.separator + ".cache" + File.separator + "RaportCache";
        }
    }

    private JasperCache() {
    }
}
