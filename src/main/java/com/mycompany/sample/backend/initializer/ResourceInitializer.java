package com.mycompany.sample.backend.initializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceInitializer {
    private static final String APP_FOLDER_NAME = "AplikacjaCmentarzy";
    private static final Logger log = LoggerFactory.getLogger("Resources");

    public static void copyDefaultResourcesToAppData() {
        Path appDataPath = getAppDataDirectory();

        // Jeśli folder już istnieje – zakładamy, że dane są już skopiowane
        if (Files.exists(appDataPath) && hasContent(appDataPath)) {
            log.info("[INFO] Folder danych już istnieje: " + appDataPath);
            return;
        }

        try {
            Files.createDirectories(appDataPath);
            copyResourceFolder("/default-data", appDataPath);
            log.info("[INFO] Skopiowano dane domyślne do: " + appDataPath);
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się skopiować danych domyślnych: " + e.getMessage(), e);
        }
    }

    private static boolean hasContent(Path dir) {
        try (Stream<Path> stream = Files.list(dir)) {
            return stream.findAny().isPresent();
        } catch (IOException e) {
            return false;
        }
    }

    public static Path getAppDataDirectory() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        Path appData;
        if (os.contains("win")) {
            String appDataEnv = System.getenv("APPDATA");
            appData = Paths.get(appDataEnv != null ? appDataEnv : userHome, APP_FOLDER_NAME);
        } else if (os.contains("mac")) {
            appData = Paths.get(userHome, "Library", "Application Support", APP_FOLDER_NAME);
        } else {
            appData = Paths.get(userHome, "." + APP_FOLDER_NAME.toLowerCase());
        }

        return appData;
    }

    private static void copyResourceFolder(String resourceFolder, Path targetDir) throws IOException {
        // Lista nazw plików do skopiowania
        String[] filesToCopy = {
                "config.json",
        };

        for (String fileName : filesToCopy) {
            try (InputStream in = ResourceInitializer.class.getResourceAsStream(resourceFolder + "/" + fileName)) {
                if (in == null) {
                    System.err.println("[WARN] Nie znaleziono zasobu: " + fileName);
                    continue;
                }

                Path targetPath = targetDir.resolve(fileName);
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
