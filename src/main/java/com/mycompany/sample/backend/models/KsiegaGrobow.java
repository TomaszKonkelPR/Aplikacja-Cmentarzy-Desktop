package com.mycompany.sample.backend.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.mycompany.sample.backend.enums.GraveAddType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@GenericGenerator(name = "kg_seq_gen", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "kg_seq"),
        @Parameter(name = "optimizer", value = "pooled-lo"),
        @Parameter(name = "increment_size", value = "50")
})
public class KsiegaGrobow {

    @Id
    @GeneratedValue(generator = "kg_seq_gen", strategy = GenerationType.SEQUENCE)
    @EqualsAndHashCode.Include
    private Long id;

    private String graveIdCode;

    private String cmentarz;

    private String rejon;

    private String kwatera;

    private String rzad;

    private String numerMiejsca;

    private LocalDate rejestrZabytkow;

    private String rodzajGrobu;

    private String lokalizacjaZDIZ;

    private GraveAddType addType;

    @Builder.Default
    @OneToMany(mappedBy = "grave", cascade = CascadeType.ALL, orphanRemoval = false)
    @OrderBy("dataPochowania ASC, id DESC")
    private List<KsiegaZmarlych> pochowani = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void computeRodzajGrobu() {
        if (this.rodzajGrobu == null || this.rodzajGrobu.isBlank()) {
            if (rejon != null && rejon.toUpperCase().startsWith("KOL")) {
                rodzajGrobu = "KOLUMBARIUM";
            } else {
                rodzajGrobu = "ZIEMNY";
            }
        }
    }

    public LocalDate getDataWaznosci() {
        return pochowani.stream()
                .map(KsiegaZmarlych::getGrobOplaconyDo)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    public int getIloscPochowanych() {
        return pochowani.size();
    }

}
