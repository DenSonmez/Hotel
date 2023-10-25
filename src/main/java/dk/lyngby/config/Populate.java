package dk.lyngby.config;


import dk.lyngby.model.Appointment;

import dk.lyngby.model.Patient;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class Populate {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            List<Patient> patients = Arrays.asList(
                    new Patient("Kiko", "Hamster", "Har haft ondt i tænderne i 3 tidligere"),
                    new Patient("Bozo", "Dog", "Havde øjenbetændelse i 2 uger"),
                    new Patient("Whiskers", "Cat", "Skader fra en slåskamp"),
                    new Patient("Buddy", "Dog", "Fødevareallergi"),
                    new Patient("Sasha", "Cat", "Hudproblemer"),
                    new Patient("Charlie", "Dog", "Hoste og nysen"),
                    new Patient("Luna", "Rabbit", "Øjenirritation"),
                    new Patient("Oscar", "Dog", "Skader fra leg i parken")
            );

            List<Appointment> appointments = Arrays.asList(
                    new Appointment(LocalDateTime.of(2023, 7, 27, 17, 30), "Tandrensning"),
                    new Appointment(LocalDateTime.of(2023, 7, 28, 18, 45), "Brækket ben"),
                    new Appointment(LocalDateTime.of(2023, 8, 28, 10, 45), "Orme kurer"),
                    new Appointment(LocalDateTime.of(2023, 9, 10, 14, 15), "Vaccination"),
                    new Appointment(LocalDateTime.of(2023, 9, 15, 9, 30), "Øreinfektion"),
                    new Appointment(LocalDateTime.of(2023, 10, 5, 11, 0), "Operation"),
                    new Appointment(LocalDateTime.of(2023, 10, 20, 13, 45), "Sårbehandling")
            );

            // Opret forhold mellem Patient og Appointment
            appointments.get(0).setPatient(new HashSet<>(patients.subList(0, 2)));
            appointments.get(1).setPatient(new HashSet<>(patients.subList(2, 3)));
            appointments.get(2).setPatient(new HashSet<>(patients.subList(3, 4)));
            appointments.get(3).setPatient(new HashSet<>(patients.subList(4, 5)));
            appointments.get(4).setPatient(new HashSet<>(patients.subList(5, 6)));
            appointments.get(5).setPatient(new HashSet<>(patients.subList(6, 7)));
            appointments.get(6).setPatient(new HashSet<>(patients.subList(7, 8)));

            // Persistér Appointment-objekter i databasen
            appointments.forEach(em::persist);

            em.getTransaction().commit();
        }
    }
}
