package com.mycompany.sample.backend.models;

import java.time.LocalDate;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.mycompany.sample.backend.generator.GenerateGraveFromCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@GenericGenerator(name = "kz_seq_gen", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "kz_seq"),
        @Parameter(name = "optimizer", value = "pooled-lo"),
        @Parameter(name = "increment_size", value = "50")
})
public class KsiegaZmarlych {

    @Id
    @GeneratedValue(generator = "kz_seq_gen", strategy = GenerationType.SEQUENCE)
    @EqualsAndHashCode.Include
    private Long id;

    private String numerEwidencyjny;

    private boolean dzieckoMartwoNarodzone;

    private String imie;

    private String nazwisko;

    private String nazwiskoRodowe;

    private String stanCywilny;

    private LocalDate dataUrodzenia;

    private String miejsceUrodzenia;

    private LocalDate dataZgonu;

    private String miejsceZgonu;

    private String ostatnieMiejsceZamieszkania;

    private boolean chorobaZakazna;

    private String przyczynaZgonu;

    private String imieOjca;

    private String imieMatki;

    private String nazwiskoOjca;

    private String nazwiskoMatki;

    private LocalDate dataPochowania;

    private LocalDate dataEkshumacji;

    private String miejscePrzedEkshumacja;

    private LocalDate dataPonownegoPochowku;

    private String miejscePonownegoPochowku;

    private String adresNowegoCmentarza;

    private String rodzajGrobu;

    private String numerAktuUSC;

    private LocalDate dataUSC;

    private String nazwaUSC;

    private String imieDysponenta;

    private String nazwiskoDysponenta;

    private String organ;

    private String pochowanyW;

    @Column(columnDefinition = "TEXT")
    private String adnotacja;

    @Column(columnDefinition = "TEXT")
    private String uwagi;

    private String miejscePochowania;

    private String lokalizacjaZDIZ;

    @Builder.Default
    private LocalDate grobOplaconyDo = null;

    private String graveIdCode;

    @ManyToOne(optional = true)
    @JoinColumn(name = "grave_id", referencedColumnName = "id", nullable = true)
    private KsiegaGrobow grave;

    public String getCemeteryFromCode() {
        if (grave != null && grave.getCmentarz() != null)
            return grave.getCmentarz();
        return GenerateGraveFromCode.partFromCode(graveIdCode, 0);
    }

    public String getRegionFromCode() {
        if (grave != null && grave.getRejon() != null)
            return grave.getRejon();
        return GenerateGraveFromCode.partFromCode(graveIdCode, 1);
    }

    public String getQuarterFromCode() {
        if (grave != null && grave.getKwatera() != null)
            return grave.getKwatera();
        return GenerateGraveFromCode.partFromCode(graveIdCode, 2); 
    }

    public String getRowFromCode() {
        if (grave != null && grave.getRzad() != null)
            return grave.getRzad();
        return GenerateGraveFromCode.partFromCode(graveIdCode, 3); 
    }

    public String getPlaceFromCode() {
        if (grave != null && grave.getNumerMiejsca() != null)
            return grave.getNumerMiejsca();
        return GenerateGraveFromCode.partFromCode(graveIdCode, 4); 
    }

    @PrePersist
    @PreUpdate
    private void aktualizujOplatePrzyZapisie() {
        if (dataPochowania != null) {
            if (grobOplaconyDo == null || grobOplaconyDo.isBefore(dataPochowania.plusYears(20))) {
                grobOplaconyDo = dataPochowania.plusYears(20);
            }
        }
    }

    public void setDataPochowania(LocalDate dataPochowania) {
        this.dataPochowania = dataPochowania;
        if (dataPochowania != null) {
            this.grobOplaconyDo = dataPochowania.plusYears(20);
        }
    }

    public void przedluzOplate(int lata) {
        if (this.grobOplaconyDo != null) {
            this.grobOplaconyDo = this.grobOplaconyDo.plusYears(lata);
        }
    }

    public void ustawDateOplaty(LocalDate nowaDataOplaty) {
        if (this.grobOplaconyDo != null) {
            this.grobOplaconyDo = nowaDataOplaty;
        }
    }
}
