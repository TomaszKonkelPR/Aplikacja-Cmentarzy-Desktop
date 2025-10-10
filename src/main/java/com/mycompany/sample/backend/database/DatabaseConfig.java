package com.mycompany.sample.backend.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.sample.backend.initializer.ResourceInitializer;

public class DatabaseConfig {
    private Map<String, Map<String, String>> cemeteries;

    public static DatabaseConfig loadConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Path appDataPath = ResourceInitializer.getAppDataDirectory();
        Path configPath = appDataPath.resolve("config.json");

        if (!Files.exists(configPath)) {
            throw new IOException("Brakuje pliku config.json w: " + configPath);
        }

        try (InputStream inputStream = Files.newInputStream(configPath)) {
            return mapper.readValue(inputStream, DatabaseConfig.class);
        }
    }

    public Map<String, String> getCredentialsFor(String cemeteryCode) {
        return cemeteries.get(cemeteryCode);
    }

    public Set<String> getAvailableCemeteries() {
        return cemeteries.keySet();
    }

    public Map<String, Map<String, String>> getCemeteries() {
        return cemeteries;
    }

    public void setCemeteries(Map<String, Map<String, String>> cemeteries) {
        this.cemeteries = cemeteries;
    }
}
