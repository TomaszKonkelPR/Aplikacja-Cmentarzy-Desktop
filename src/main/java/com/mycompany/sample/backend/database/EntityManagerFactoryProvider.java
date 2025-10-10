package com.mycompany.sample.backend.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

public class EntityManagerFactoryProvider {
    private static final Logger log = LoggerFactory.getLogger("Database");

    public static DbResources createResources(Map<String, String> dbProps) {
        HikariDataSource ds = buildHikari(dbProps);
        String schema = dbProps.getOrDefault("schema", "public");
        List<String> required = parseRequiredTables(
                dbProps.getOrDefault("requiredTables", "KsiegaGrobow,KsiegaZmarlych"));

        // jedna, wspólna mapa props:
        Map<String, Object> props = new HashMap<>();
        props.put(AvailableSettings.DATASOURCE, ds);
        props.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.default_schema", schema);

        // optymalizacje batch
        props.put("hibernate.jdbc.batch_size", "1000");
        props.put("hibernate.order_inserts", "true");
        props.put("hibernate.order_updates", "true");
        props.put("hibernate.batch_versioned_data", "true");

        try {
            // jeśli brakuje wymaganych tabel → jednorazowy 'update'
            if (!allTablesExist(ds, schema, required)) {
                log.info("[DB INIT] Brakuje wymaganych tabel → jednorazowy 'update' (bootstrap)...");
                props.put("hibernate.hbm2ddl.auto", "update");
                try (EntityManagerFactory ignored = Persistence.createEntityManagerFactory("myPU", props)) {
                    // samo utworzy struktury na podstawie encji
                }
            }

            // docelowo zawsze validate
            log.info("[DB INIT] Start w trybie 'validate'...");
            props.put("hibernate.hbm2ddl.auto", "validate");
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU", props);
            log.info("[DB INIT] 'validate' OK.");
            return new DbResources(emf, ds);

        } catch (PersistenceException ex) {
            // gdy 'validate' padnie na schemacie → dobuduj i wróć na validate
            if (isSchemaProblem(ex)) {
                log.info("[DB INIT] 'validate' zgłosił problem schematu → 'update' i ponownie 'validate'...");
                try {
                    props.put("hibernate.hbm2ddl.auto", "update");
                    try (EntityManagerFactory ignored = Persistence.createEntityManagerFactory("myPU", props)) {
                    }

                    props.put("hibernate.hbm2ddl.auto", "validate");
                    EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU", props);
                    return new DbResources(emf, ds);
                } catch (RuntimeException re) {
                    ds.close();
                    throw re;
                }
            }
            ds.close();
            throw ex;
        } catch (RuntimeException e) {
            ds.close();
            throw e;
        }
    }

    private static boolean isSchemaProblem(Throwable ex) {
        while (ex != null) {
            if (ex instanceof SchemaManagementException)
                return true;
            ex = ex.getCause();
        }
        return false;
    }

    private static List<String> parseRequiredTables(String csv) {
        List<String> out = new ArrayList<>();
        for (String s : csv.split(",")) {
            String t = s.trim();
            if (!t.isEmpty())
                out.add(t);
        }
        return out;
    }

    private static boolean allTablesExist(HikariDataSource ds, String schema, List<String> tables) {
        for (String t : tables)
            if (!tableExists(ds, schema, t))
                return false;
        return true;
    }

    private static boolean tableExists(HikariDataSource ds, String schema, String table) {
        final String q = "select coalesce(to_regclass(?), to_regclass(?)) is not null";
        final String unquoted = schema + "." + table; 
        final String lower = schema + "." + table.toLowerCase();
        try (Connection c = ds.getConnection();
                PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, unquoted);
            ps.setString(2, lower);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd sprawdzania tabeli: " + unquoted, e);
        }
    }

    private static HikariDataSource buildHikari(Map<String, String> dbProps) {
        String url = Objects.requireNonNull(dbProps.get("url"), "Brak 'url' w configu");
        String user = Objects.requireNonNull(dbProps.get("user"), "Brak 'user' w configu");
        String pwd = Objects.requireNonNull(dbProps.get("password"), "Brak 'password' w configu");

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(pwd);

        cfg.setMaximumPoolSize(parseInt(dbProps.getOrDefault("pool.max", "6"), 6));
        cfg.setMinimumIdle(parseInt(dbProps.getOrDefault("pool.minIdle", "1"), 1));
        cfg.setIdleTimeout(parseLong(dbProps.getOrDefault("pool.idleTimeoutMs", "600000"), 600000L));
        cfg.setConnectionTimeout(parseLong(dbProps.getOrDefault("pool.connTimeoutMs", "30000"), 30000L));
        cfg.setKeepaliveTime(parseLong(dbProps.getOrDefault("pool.keepaliveMs", "300000"), 300000L));
        cfg.setValidationTimeout(parseLong(dbProps.getOrDefault("pool.validationTimeoutMs", "5000"), 5000L));
        cfg.setLeakDetectionThreshold(parseLong(dbProps.getOrDefault("pool.leakDetectMs", "0"), 0L));

        String sslMode = dbProps.getOrDefault("sslmode", "disable");
        cfg.addDataSourceProperty("sslmode", sslMode);
        cfg.addDataSourceProperty("ApplicationName", dbProps.getOrDefault("applicationName", "cemetery-app"));
        cfg.addDataSourceProperty("reWriteBatchedInserts", "true");
        cfg.addDataSourceProperty("tcpKeepAlive", "true");
        cfg.addDataSourceProperty("loginTimeout", "30");
        cfg.addDataSourceProperty("socketTimeout", "0");
        cfg.addDataSourceProperty("prepareThreshold", "3"); 

        log.info("Łączenie z bazą: url={} user={}", url, user);

        return new HikariDataSource(cfg);
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private static long parseLong(String s, long def) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return def;
        }
    }

}
