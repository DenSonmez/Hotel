package dk.lyngby.model;

import dk.lyngby.dto.PatientDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id", nullable = false, unique = true)
    private Integer patientId;

    @Setter
    @Column(name = "patient_name", nullable = false)
    private String name;

    @Setter
    @Column(name = "species", nullable = false)
    private String species;

    @Setter
    @Column(name = "medicalHistory", nullable = false)
    private String medicalHistory;

    @Setter
    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    public Patient(String name, String species, String medicalHistory) {
        this.name = name;
        this.species = species;
        this.medicalHistory = medicalHistory;

    }



    }
