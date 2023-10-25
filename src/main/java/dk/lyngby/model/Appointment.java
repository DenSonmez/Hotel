package dk.lyngby.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id", nullable = false, unique = true)
    private Integer appointmentId;

    @Setter
    @Column(name = "appointmentDate", nullable = false)
    private LocalDateTime appointmentDate;

    @Setter
    @Column(name = "reason", nullable = false)
    private String reason;


    @OneToMany(mappedBy = "appointment", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Patient> patients = new HashSet<>();


    public Appointment(LocalDateTime appointmentDate, String reason) {
        this.appointmentDate = appointmentDate;
        this.reason = reason;

    }

    // den her metode bruges til at sætte et patients til et Appointment.
    public void setPatient(Set<Patient> patients) {
        if (patients != null) {
            this.patients = patients;
            for (Patient p : patients) {
                p.setAppointment(this);
            }
        }
    }


    //den her metode bruges til at tilføje et patients til et Appointment
    public void addPatients(Patient patients) {
        if (patients != null) {
            this.patients.add(patients);
            patients.setAppointment(this);
        }
    }


}
