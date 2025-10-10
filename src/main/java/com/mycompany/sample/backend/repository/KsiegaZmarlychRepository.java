package com.mycompany.sample.backend.repository;

import java.time.LocalDate;
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
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

public class KsiegaZmarlychRepository {
    private final EntityManagerFactory emf;
    private static final Logger log = LoggerFactory.getLogger("DeceasedRepo");

    public KsiegaZmarlychRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void saveAllWithGraveRefs(List<KsiegaZmarlych> deceased, Map<String, Long> codeToGraveId) {
        if (deceased == null || deceased.isEmpty())
            return;

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            final int batchSize = 100;

            for (int i = 0; i < deceased.size(); i++) {
                KsiegaZmarlych z = deceased.get(i);

                if (z.getId() != null) {
                    throw new IllegalArgumentException("saveAllWithGraveRefs przyjmuje tylko nowe encje (id == null)");
                }

                String code = z.getGraveIdCode();
                Long graveId = (code != null) ? codeToGraveId.get(code) : null;
                if (graveId != null) {
                    z.setGrave(em.getReference(KsiegaGrobow.class, graveId));
                } else {
                }

                em.persist(z);

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

    public void saveAllForSingleGrave(List<KsiegaZmarlych> deceased, KsiegaGrobow grave) {
        if (deceased == null || deceased.isEmpty()) {
            return;
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            final int batchSize = 100;

            KsiegaGrobow managedGrave = em.getReference(KsiegaGrobow.class, grave.getId());

            for (int i = 0; i < deceased.size(); i++) {
                KsiegaZmarlych z = deceased.get(i);

                z.setGrave(managedGrave);
                z.setGraveIdCode(managedGrave.getGraveIdCode());

                em.persist(z);

                if ((i + 1) % batchSize == 0) {
                    em.flush();
                    em.clear();
                    managedGrave = em.getReference(KsiegaGrobow.class, grave.getId());
                }
            }
            em.flush();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateAll(List<KsiegaZmarlych> deceased) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            final int batchSize = 100;

            for (int i = 0; i < deceased.size(); i++) {
                em.merge(deceased.get(i));

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

    public void updateDeceasedWithNewCode(Long deceasedId, String newCode) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            KsiegaZmarlych z = em.find(KsiegaZmarlych.class, deceasedId);
            if (z == null) {
                throw new IllegalArgumentException("Nie znaleziono zmarłego o ID: " + deceasedId);
            }

            if (newCode != null && newCode.matches("[A-Z]{2,4}///")) {
                KsiegaGrobow old = z.getGrave();
                if (old != null) {
                    old.getPochowani().remove(z);
                }

                z.setGrave(null);
                z.setGraveIdCode(newCode);

                em.flush();
                tx.commit();
                return;
            }

            KsiegaGrobow newGrave = em.createQuery(
                    "SELECT g FROM KsiegaGrobow g WHERE g.graveIdCode = :code", KsiegaGrobow.class)
                    .setParameter("code", newCode)
                    .getSingleResult();

            KsiegaGrobow old = z.getGrave();
            if (old != null) {
                old.getPochowani().remove(z);
            }

            z.setGrave(newGrave);
            newGrave.getPochowani().add(z);
            z.setGraveIdCode(newCode);

            em.flush();
            tx.commit();
        } catch (NoResultException e) {
            if (tx.isActive())
                tx.rollback();
            throw new IllegalArgumentException("Nie znaleziono grobu o kodzie: " + newCode, e);
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public long count() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(g) FROM KsiegaZmarlych g", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public long countListOfDeceasedByPattern(String pattern, String firstname, String surname) {
        EntityManager em = emf.createEntityManager();
        try {
            String first = firstname != null ? firstname.toLowerCase() : "";
            String last = surname != null ? surname.toLowerCase() : "";

            boolean hasFirstname = !first.isEmpty();
            boolean hasSurname = !last.isEmpty();
            boolean firstnameIsBlank = first.equals(" ");
            boolean surnameIsBlank = last.equals(" ");

            StringBuilder jpql = new StringBuilder("""
                        SELECT COUNT(z)
                        FROM KsiegaZmarlych z
                        WHERE z.graveIdCode LIKE :pattern AND imie != 'LIKWIDACJA'
                    """);

            if (hasFirstname && !firstnameIsBlank) {
                jpql.append(" AND LOWER(TRIM(z.imie)) LIKE :firstname ");
            } else if (firstnameIsBlank) {
                jpql.append(" AND (z.imie IS NULL OR TRIM(z.imie) = '') ");
            }

            if (hasSurname && !surnameIsBlank) {
                jpql.append(" AND LOWER(TRIM(z.nazwisko)) = :surname ");
            } else if (surnameIsBlank) {
                jpql.append(" AND (z.nazwisko IS NULL OR TRIM(z.nazwisko) = '') ");
            }

            TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class)
                    .setParameter("pattern", pattern);

            if (hasFirstname && !firstnameIsBlank) {
                query.setParameter("firstname", "%" + first + "%");
            }

            if (hasSurname && !surnameIsBlank) {
                query.setParameter("surname", last);
            }

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<KsiegaZmarlych> getListOfDeceasedByPattern(String pattern, int pageIndex, int pageSize,
            String firstname, String surname) {
        log.info("Ładuję dane zmarłych dla: " + pattern);
        EntityManager em = emf.createEntityManager();
        try {
            String first = firstname != null ? firstname.toLowerCase() : "";
            String last = surname != null ? surname.toLowerCase() : "";

            boolean hasFirstname = !first.isEmpty();
            boolean hasSurname = !last.isEmpty();
            boolean firstnameIsBlank = first.equals(" ");
            boolean surnameIsBlank = last.equals(" ");

            StringBuilder sql = new StringBuilder("""
                        SELECT *
                        FROM KsiegaZmarlych z
                        WHERE z.graveidcode LIKE :pattern AND imie != 'LIKWIDACJA'
                    """);

            if (hasFirstname && !firstnameIsBlank) {
                sql.append(" AND LOWER(TRIM(z.imie)) LIKE :firstname ");
            } else if (firstnameIsBlank) {
                sql.append(" AND (z.imie IS NULL OR TRIM(z.imie) = '') ");
            }

            if (hasSurname && !surnameIsBlank) {
                sql.append(" AND LOWER(TRIM(z.nazwisko)) = :surname ");
            } else if (surnameIsBlank) {
                sql.append(" AND (z.nazwisko IS NULL OR TRIM(z.nazwisko) = '') ");
            }

            sql.append("""
                        ORDER BY
                            CASE WHEN z.grave_id IS NULL THEN 1 ELSE 0 END DESC,
                            z.graveidcode,
                            z.dataPochowania DESC
                    """);

            Query query = em.createNativeQuery(sql.toString(), KsiegaZmarlych.class)
                    .setParameter("pattern", pattern)
                    .setFirstResult(pageIndex * pageSize)
                    .setMaxResults(pageSize);

            if (!firstnameIsBlank && !first.isEmpty()) {
                query.setParameter("firstname", "%" + first + "%");
            }

            if (!surnameIsBlank && !last.isEmpty()) {
                query.setParameter("surname", last);
            }

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long countListOfDeceasedWithoutGrave(String firstname, String surname) {
        EntityManager em = emf.createEntityManager();
        try {
            String first = firstname != null ? firstname.toLowerCase() : "";
            String last = surname != null ? surname.toLowerCase() : "";

            boolean hasFirstname = !first.isEmpty();
            boolean hasSurname = !last.isEmpty();
            boolean firstnameIsBlank = first.equals(" ");
            boolean surnameIsBlank = last.equals(" ");

            StringBuilder jpql = new StringBuilder("""
                        SELECT COUNT(z)
                        FROM KsiegaZmarlych z
                        WHERE 1=1
                    """);

            if (hasFirstname && !firstnameIsBlank) {
                jpql.append(" AND LOWER(TRIM(z.imie)) LIKE :firstname ");
            } else if (firstnameIsBlank) {
                jpql.append(" AND (z.imie IS NULL OR TRIM(z.imie) = '') ");
            }

            if (hasSurname && !surnameIsBlank) {
                jpql.append(" AND LOWER(TRIM(z.nazwisko)) = :surname ");
            } else if (surnameIsBlank) {
                jpql.append(" AND (z.nazwisko IS NULL OR TRIM(z.nazwisko) = '') ");
            }

            TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);

            if (!firstnameIsBlank && !first.isEmpty()) {
                query.setParameter("firstname", "%" + first + "%");
            }

            if (!surnameIsBlank && !last.isEmpty()) {
                query.setParameter("surname", last);
            }

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<KsiegaZmarlych> getListOfDeceasedWithoutGrave(int pageIndex, int pageSize,
            String firstname, String surname) {
        log.info("Ładuję dane zmarłych bez grobu");
        EntityManager em = emf.createEntityManager();
        try {
            String first = firstname != null ? firstname.toLowerCase() : "";
            String last = surname != null ? surname.toLowerCase() : "";

            boolean hasFirstname = !first.isEmpty();
            boolean hasSurname = !last.isEmpty();
            boolean firstnameIsBlank = first.equals(" ");
            boolean surnameIsBlank = last.equals(" ");

            StringBuilder sql = new StringBuilder("""
                        SELECT *
                        FROM KsiegaZmarlych z
                        WHERE 1=1
                    """);

            if (hasFirstname && !firstnameIsBlank) {
                sql.append(" AND LOWER(TRIM(z.imie)) LIKE :firstname ");
            } else if (firstnameIsBlank) {
                sql.append(" AND (z.imie IS NULL OR TRIM(z.imie) = '') ");
            }

            if (hasSurname && !surnameIsBlank) {
                sql.append(" AND LOWER(TRIM(z.nazwisko)) = :surname ");
            } else if (surnameIsBlank) {
                sql.append(" AND (z.nazwisko IS NULL OR TRIM(z.nazwisko) = '') ");
            }

            sql.append("""
                        ORDER BY
                            CASE WHEN z.grave_id IS NULL THEN 1 ELSE 0 END DESC,
                            z.dataPochowania DESC NULLS LAST
                    """);

            Query query = em.createNativeQuery(sql.toString(), KsiegaZmarlych.class)
                    .setFirstResult(pageIndex * pageSize)
                    .setMaxResults(pageSize);

            if (!firstnameIsBlank && !first.isEmpty()) {
                query.setParameter("firstname", "%" + first + "%");
            }

            if (!surnameIsBlank && !last.isEmpty()) {
                query.setParameter("surname", last);
            }

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<KsiegaZmarlych> getListOfDeceasedByNameAndSurname(String firstname, String surname) {
        log.info("Ładuję dane zmarłych według podanych danych");

        EntityManager em = emf.createEntityManager();
        try {
            String first = firstname != null ? firstname.toLowerCase() : "";
            String last = surname != null ? surname.toLowerCase() : "";
            boolean hasFirstname = !first.isEmpty();
            boolean hasSurname = !last.isEmpty();
            boolean firstnameIsBlank = first.equals(" ");
            boolean surnameIsBlank = last.equals(" ");

            if (!hasFirstname && !hasSurname) {
                log.info("Brak danych do wyszukiwania – zwracam pustą listę");
                return List.of();
            }

            StringBuilder jpql = new StringBuilder("""
                        SELECT z FROM KsiegaZmarlych z
                        LEFT JOIN z.grave g
                        WHERE 1=1
                    """);

            if (hasFirstname && !firstnameIsBlank) {
                jpql.append(" AND LOWER(TRIM(z.imie)) LIKE :firstnameAnywhere ");
                if (!hasSurname || surnameIsBlank) {
                    jpql.append(" AND (z.nazwisko IS NULL OR TRIM(z.nazwisko) = '') ");
                }
            } else if (firstnameIsBlank) {
                jpql.append(" AND (z.imie IS NULL OR TRIM(z.imie) = '') ");
            }

            if (hasSurname && !surnameIsBlank) {
                jpql.append(" AND LOWER(TRIM(z.nazwisko)) = :surnameExact ");
            } else if (surnameIsBlank) {
                jpql.append(" AND (z.nazwisko IS NULL OR TRIM(z.nazwisko) = '') ");
            }

            jpql.append(" ORDER BY z.dataPochowania");

            TypedQuery<KsiegaZmarlych> query = em.createQuery(jpql.toString(), KsiegaZmarlych.class);

            if (!firstnameIsBlank && !first.isEmpty()) {
                query.setParameter("firstnameAnywhere", "%" + first + "%");
            }

            if (!surnameIsBlank && !last.isEmpty()) {
                query.setParameter("surnameExact", last);
            }

            List<KsiegaZmarlych> result = query.getResultList();

            for (KsiegaZmarlych z : result) {
                if (z.getGrave() != null) {
                    z.getGrave().setPochowani(null);
                }
            }

            return result;
        } finally {
            em.close();
        }
    }

    public void extendPayForGrave(Long deceasedId, int years) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            KsiegaZmarlych zmarly = em.find(KsiegaZmarlych.class, deceasedId);
            if (zmarly == null) {
                throw new IllegalArgumentException("Nie znaleziono zmarłego o ID " + deceasedId);
            }

            zmarly.przedluzOplate(years);

            em.merge(zmarly);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw e;
        } finally {
            em.close();
        }

    }

    public void setNewPayDate(Long deceasedId, LocalDate newPayDate) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            KsiegaZmarlych zmarly = em.find(KsiegaZmarlych.class, deceasedId);
            if (zmarly == null) {
                throw new IllegalArgumentException("Nie znaleziono zmarłego o ID " + deceasedId);
            }

            zmarly.ustawDateOplaty(newPayDate);

            em.merge(zmarly);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
