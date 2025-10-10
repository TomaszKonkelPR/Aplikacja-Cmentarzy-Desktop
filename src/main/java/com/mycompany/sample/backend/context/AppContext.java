package com.mycompany.sample.backend.context;

import java.util.Map;

import com.mycompany.sample.backend.database.DbResources;
import com.mycompany.sample.backend.database.EntityManagerFactoryProvider;
import com.mycompany.sample.backend.service.models.KsiegaGrobowService;
import com.mycompany.sample.backend.service.models.KsiegaZmarlychService;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

public class AppContext {
    private static String selectedCemetery;
    private static String selectedCemeteryDisplayName;

    private static EntityManagerFactory emf;
    private static HikariDataSource ds;

    private static KsiegaGrobowService graveService;
    private static KsiegaZmarlychService deceasedService;

    public static synchronized void switchDatabase(Map<String,String> dbProps) {
        // zamknij stare zasoby
        shutdown();

        // utwórz nowe
        DbResources res = EntityManagerFactoryProvider.createResources(dbProps);
        emf = res.emf();
        ds  = res.ds();

        graveService    = new KsiegaGrobowService(emf);
        deceasedService = new KsiegaZmarlychService(graveService, emf);
    }

    public static synchronized EntityManagerFactory emf() {
        if (emf == null) throw new IllegalStateException("EMF not initialized. Call switchDatabase first.");
        return emf;
    }

    public static synchronized KsiegaGrobowService graveService() {
        if (graveService == null) throw new IllegalStateException("Services not initialized. Call switchDatabase first.");
        return graveService;
    }

    public static synchronized KsiegaZmarlychService deceasedService() {
        if (deceasedService == null) throw new IllegalStateException("Services not initialized. Call switchDatabase first.");
        return deceasedService;
    }

    public static synchronized void shutdown() {
        if (emf != null) {
            try { emf.close(); } catch (Exception ignore) {}
            emf = null;
        }
        if (ds != null) {
            try { ds.close(); } catch (Exception ignore) {}
            ds = null;
        }
        graveService = null;
        deceasedService = null;
    }

    public static void setSelectedCemetery(String code, String displayName) {
        selectedCemetery = code;
        selectedCemeteryDisplayName = displayName;
    }

    public static String getSelectedCemetery() {
        return selectedCemetery;
    }

    public static void setSelectedCemetery(String cemetery) {
        selectedCemetery = cemetery;
    }

    public static String getSelectedCemeteryDisplayName() {
        return selectedCemeteryDisplayName;
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(AppContext::shutdown));
    }
}
