package com.mycompany.sample.backend.repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.sample.backend.models.KsiegaGrobow;
import com.mycompany.sample.backend.models.KsiegaZmarlych;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class KsiegaGrobowRepository {
    private final EntityManagerFactory emf;
    private static final Logger log = LoggerFactory.getLogger("GraveRepo");

    public KsiegaGrobowRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void saveAll(List<KsiegaGrobow> entities) {
        if (entities == null || entities.isEmpty())
            return;

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            final int batchSize = 100;

            for (int i = 0; i < entities.size(); i++) {
                KsiegaGrobow g = entities.get(i);

                if (g.getId() != null) {
                    throw new IllegalArgumentException("saveAll przyjmuje tylko nowe encje (id == null)");
                }
                em.persist(g);

                if ((i + 1) % batchSize == 0) {
                    em.flush();
                    em.clear();
                }
            }

            em.flush();
            tx.commit();

        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public int cleanupDuplicatesKeepMinId() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            String deleteSql = """
                    DELETE FROM ksiegagrobow
                    WHERE id NOT IN (
                      SELECT MIN(id)
                      FROM ksiegagrobow
                      GROUP BY graveidcode
                    )
                    """;

            int deleted = em.createNativeQuery(deleteSql).executeUpdate();
            tx.commit();
            log.info("Duplikaty faktycznie usunięte={}", deleted);
            return deleted;
        } catch (RuntimeException e) {
            if (tx.isActive())
                tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public String saveNewGrave(KsiegaGrobow g) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            KsiegaGrobow target;
            var existing = em.createQuery("""
                    SELECT x FROM KsiegaGrobow x
                    WHERE x.graveIdCode = :code
                    """, KsiegaGrobow.class)
                    .setParameter("code", g.getGraveIdCode())
                    .setMaxResults(1)
                    .getResultList();

            boolean existed = !existing.isEmpty();
            boolean newQuarter = false;
            boolean newRegion = false;

            if (existed) {
                target = existing.get(0);
            } else {
                boolean quarterExists = !em.createQuery("""
                        SELECT 1 FROM KsiegaGrobow x
                        WHERE x.cmentarz = :cem AND x.kwatera = :kw
                        """, Integer.class)
                        .setParameter("cem", g.getCmentarz())
                        .setParameter("kw", g.getKwatera())
                        .setMaxResults(1)
                        .getResultList()
                        .isEmpty();
                newQuarter = !quarterExists;

                if (g.getRejon() != null && !g.getRejon().isBlank()) {
                    boolean regionExists = !em.createQuery("""
                            SELECT 1 FROM KsiegaGrobow x
                            WHERE x.cmentarz = :cem AND x.rejon = :reg
                            """, Integer.class)
                            .setParameter("cem", g.getCmentarz())
                            .setParameter("reg", g.getRejon())
                            .setMaxResults(1)
                            .getResultList()
                            .isEmpty();
                    newRegion = !regionExists;
                }
                em.persist(g);
                target = g;
            }

            var toAttach = em.createQuery("""
                    SELECT z FROM KsiegaZmarlych z
                    WHERE z.graveIdCode = :code AND z.grave IS NULL
                    """, KsiegaZmarlych.class)
                    .setParameter("code", g.getGraveIdCode())
                    .getResultList();

            for (KsiegaZmarlych z : toAttach) {
                z.setGrave(target);
                em.merge(z);
            }
            long attached = toAttach.size();

            tx.commit();

            StringBuilder msg = new StringBuilder();
            msg.append(existed ? "Grób już istniał w bazie.\n" : "Zapisano nowy grób.\n");
            msg.append("Kod: ").append(g.getGraveIdCode());
            if (attached > 0)
                msg.append("\nPowiązano zmarłych: ").append(attached);
            if (!existed) {
                if (newQuarter)
                    msg.append("\nNowa kwatera w cmentarzu: ").append(g.getKwatera());
                if (newRegion)
                    msg.append("\nNowy rejon w cmentarzu: ").append(g.getRejon());
            }
            return msg.toString();

        } catch (RuntimeException ex) {
            if (tx.isActive())
                tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public long count() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(g) FROM KsiegaGrobow g", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<KsiegaGrobow> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT g FROM KsiegaGrobow g ", KsiegaGrobow.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<String> getListOfCemetery() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT g.cmentarz FROM KsiegaGrobow g ORDER BY g.cmentarz",
                    String.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<String> getListOfAllRegionsByCemetery(String cemetery) {
        EntityManager em = emf.createEntityManager();
        try {
            List<String> res = em.createQuery(
                    "SELECT DISTINCT g.rejon FROM KsiegaGrobow g WHERE g.cmentarz = :cemetery ORDER BY g.rejon",
                    String.class)
                    .setParameter("cemetery", cemetery)
                    .getResultList();
            return res;
        } finally {
            em.close();
        }
    }

    public List<String> getListOfColumbariumByCemetery(String cemetery) {
        EntityManager em = emf.createEntityManager();
        try {
            List<String> res = em.createQuery("""
                        SELECT DISTINCT g.rejon
                        FROM KsiegaGrobow g
                        WHERE g.graveIdCode LIKE :cemetery || '/KOL%' AND g.cmentarz = :cemetery
                        ORDER BY g.rejon
                    """,
                    String.class)
                    .setParameter("cemetery", cemetery)
                    .getResultList();
            return res;
        } finally {
            em.close();
        }
    }

    public List<String> getListOfRegionByCemetery(String cemetery) {
        EntityManager em = emf.createEntityManager();
        try {
            List<String> res = em.createQuery("""
                        SELECT DISTINCT g.rejon
                        FROM KsiegaGrobow g
                        WHERE g.graveIdCode NOT LIKE :cemetery || '/KOL%' AND g.cmentarz = :cemetery
                        ORDER BY g.rejon
                    """,
                    String.class)
                    .setParameter("cemetery", cemetery)
                    .getResultList();
            return res;
        } finally {
            em.close();
        }
    }

    public List<String> getListOfQuarterForRegionsByCemetery(String cemetery, String region) {
        EntityManager em = emf.createEntityManager();
        try {
            List<String> res = em.createQuery("""
                        SELECT DISTINCT g.kwatera
                        FROM KsiegaGrobow g
                        WHERE g.rejon = :region AND g.cmentarz = :cemetery
                    """, String.class)
                    .setParameter("cemetery", cemetery)
                    .setParameter("region", region)
                    .getResultList();
            return res;
        } finally {
            em.close();
        }
    }

    public List<String> getListOfRowsByCemeteryAndQuarter(String cemetery, String region, String quarter) {
        EntityManager em = emf.createEntityManager();
        try {
            List<String> res = em.createQuery("""
                        SELECT DISTINCT g.rzad
                        FROM KsiegaGrobow g
                        WHERE g.kwatera = :quarter AND g.rejon = :region AND g.cmentarz = :cemetery
                    """, String.class)
                    .setParameter("cemetery", cemetery)
                    .setParameter("region", region)
                    .setParameter("quarter", quarter)
                    .getResultList();
            return res;
        } finally {
            em.close();
        }
    }

    public List<String> getListOfPlacesByCemeteryAndQuarterAndRow(String cemetery, String region, String quarter, String row) {
        EntityManager em = emf.createEntityManager();
        try {
            List<String> res = em.createQuery("""
                        SELECT DISTINCT g.numerMiejsca
                        FROM KsiegaGrobow g
                        WHERE g.rzad = :row AND g.kwatera = :quarter AND g.rejon = :region AND g.cmentarz = :cemetery
                    """, String.class)
                    .setParameter("cemetery", cemetery)
                    .setParameter("region", region)
                    .setParameter("quarter", quarter)
                    .setParameter("row", row)
                    .getResultList();
            return res;
        } finally {
            em.close();
        }
    }

    public List<KsiegaGrobow> getGravesByCemeteryAndQuarter(String cemetery, String region, String quarter) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Long> orderedIds = em.createNativeQuery("""
                        SELECT g.id
                        FROM KsiegaGrobow g
                        WHERE g.kwatera = :quarter AND g.rejon = :region AND g.cmentarz = :cemetery
                        ORDER BY
                        /* --- RZĄD: najpierw część liczbowa --- */
                          COALESCE(CAST(NULLIF(substring(g.rzad from '^([0-9]+)'), '') AS int), 2147483647),
                          /* --- RZĄD: potem ewentualny sufiks po liczbie --- */
                          COALESCE(substring(g.rzad from '^[0-9]+(.*)$'), ''),
                          /* --- MIEJSCE: najpierw część liczbowa --- */
                          COALESCE(CAST(NULLIF(substring(g.numerMiejsca from '^([0-9]+)'), '') AS int), 2147483647),
                          /* --- MIEJSCE: potem ewentualny sufiks po liczbie --- */
                          COALESCE(substring(g.numerMiejsca from '^[0-9]+(.*)$'), ''),
                          g.id
                    """)
                    .setParameter("cemetery", cemetery)
                    .setParameter("region", region)
                    .setParameter("quarter", quarter)
                    .getResultList()
                    .stream()
                    .map(o -> ((Number) o).longValue())
                    .toList();

            if (orderedIds.isEmpty())
                return List.of();

            List<KsiegaGrobow> fetched = em.createQuery("""
                        SELECT DISTINCT g
                        FROM KsiegaGrobow g
                        LEFT JOIN FETCH g.pochowani p
                        WHERE g.id IN :ids
                    """, KsiegaGrobow.class)
                    .setParameter("ids", orderedIds)
                    .getResultList();

            Map<Long, Integer> pos = new HashMap<>();
            for (int i = 0; i < orderedIds.size(); i++)
                pos.put(orderedIds.get(i), i);

            fetched.sort(Comparator.comparingInt(g -> pos.getOrDefault(g.getId(), Integer.MAX_VALUE)));
            return fetched;
        } finally {
            em.close();
        }
    }

    public KsiegaGrobow findByGraveIdCode(String graveIdCode) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT g FROM KsiegaGrobow g LEFT JOIN FETCH g.pochowani WHERE g.graveIdCode = :code",
                    KsiegaGrobow.class)
                    .setParameter("code", graveIdCode)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public KsiegaGrobow findByGraveId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT g FROM KsiegaGrobow g LEFT JOIN FETCH g.pochowani WHERE g.id = :id",
                    KsiegaGrobow.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void updateRejestrZabytkowDate(Long id, LocalDate newDate) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            KsiegaGrobow grave = em.find(KsiegaGrobow.class, id);
            if (grave == null) {
                throw new IllegalArgumentException("Nie znaleziono grobu o ID " + id);
            }

            grave.setRejestrZabytkow(newDate);
            em.merge(grave);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateGraveType(Long id, String newType) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            KsiegaGrobow grave = em.find(KsiegaGrobow.class, id);
            if (grave == null) {
                throw new IllegalArgumentException("Nie znaleziono grobu o ID " + id);
            }

            grave.setRodzajGrobu(newType);
            em.merge(grave);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateAll(List<KsiegaGrobow> grave) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int batchSize = 100;

            for (int i = 0; i < grave.size(); i++) {
                em.merge(grave.get(i));

                if (i > 0 && i % batchSize == 0) {
                    em.flush();
                    em.clear();
                }
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

}
