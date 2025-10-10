package com.mycompany.sample.backend.database;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;

public record DbResources(EntityManagerFactory emf, HikariDataSource ds) {}
