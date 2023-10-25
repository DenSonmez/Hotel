package dk.lyngby.dao.impl;

import dk.lyngby.model.Appointment;
import dk.lyngby.model.Patient;
import jakarta.persistence.EntityManagerFactory;



public class PatientDAO extends GenericDAO<Patient, Integer> {

    private static PatientDAO instance;
    private static EntityManagerFactory emf;

    public PatientDAO(Class<Patient> entityClass, EntityManagerFactory emf) {
        super(entityClass, emf);
        PatientDAO.emf = emf;
    }

    public static PatientDAO getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new PatientDAO(Patient.class, emf);
        }
        return instance;
    }



    public Appointment addPatientToAppointment(Integer appointmentId, Patient patient) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            var appointment = em.find(Appointment.class, appointmentId);
            appointment.addPatients(patient);
            em.persist(appointment);
            Appointment merge = em.merge(appointment);
            em.getTransaction().commit();
            return merge;
        }
    }

    public Boolean validateAppointmentTime(Integer patientId, Integer appointmentId) {
        try (var em = emf.createEntityManager()) {
            var query = em.createQuery("SELECT p FROM Patient p WHERE p.patientId = :patientId and p.appointment.id = :appointmentId", Patient.class);
            query.setParameter("patientId", patientId);
            query.setParameter("appointmentId", appointmentId);
            return !query.getResultList().isEmpty();
        }
    }




}








/*
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RoomDAO implements IDAO<Room, Integer> {

    private static RoomDAO instance;
    private static EntityManagerFactory emf;

    public static RoomDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new RoomDAO();
        }
        return instance;
    }

    public Hotel addRoomToHotel(Integer hotelId, Room room ) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            var hotel = em.find(Hotel.class, hotelId);
            hotel.addRoom(room);
            em.persist(room);
            Hotel merge = em.merge(hotel);
            em.getTransaction().commit();
            return merge;
        }
    }

    @Override
    public Room read(Integer integer) {
        try (var em = emf.createEntityManager()) {
            return em.find(Room.class, integer);
        }
    }

    @Override
    public List<Room> readAll() {
        try (var em = emf.createEntityManager()) {
            var query = em.createQuery("SELECT r FROM Room r", Room.class);
            return query.getResultList();
        }
    }

    public List<Room> findAllByPrice(Integer priceFrom, Integer priceTo) {
        try (var em = emf.createEntityManager()) {
            //her vil vi gerne have en liste af alle rum der er mellem priceFrom og priceTo
            var query = em.createQuery("SELECT r FROM Room r WHERE r.roomPrice < :priceFrom and r.roomPrice > :priceTo", Room.class);
            query.setParameter("priceFrom", priceFrom);
            query.setParameter("priceTo", priceTo);
            return query.getResultList();
        }
    }

    @Override
    public Room create(Room room) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(room);
            em.getTransaction().commit();
            return room;
        }
    }

    @Override
    public Room update(Integer integer, Room room) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            var r = em.find(Room.class, integer);
            r.setRoomNumber(room.getRoomNumber());
            r.setRoomType(room.getRoomType());
            r.setRoomPrice(room.getRoomPrice());

            Room merge = em.merge(r);
            em.getTransaction().commit();
            return merge;
        }
    }

    @Override
    public void delete(Integer integer) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            var room = em.find(Room.class, integer);
            em.remove(room);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (var em = emf.createEntityManager()) {
            var room = em.find(Room.class, integer);
            return room != null;
        }
    }

   */
/* public Function<Integer, Boolean> validateHotelRoomNumber = (roomNumber) -> {
        try (var em = emf.createEntityManager()) {
            var room = em.find(Room.class, roomNumber);
            return room != null;
        }
    };*//*


    //Sammenfattende bruges denne metode til at validere, om et bestemt værelsesnummer eksisterer inden for det specificerede hotel.
    // Hvis et sådant værelsesnummer findes i hotellet, returneres true; ellers returneres false.
    // Det er nyttigt, når du vil kontrollere,
    // om et værelsesnummer er gyldigt for et bestemt hotel i din database.
    public Boolean validateHotelRoomNumber(Integer roomNumber, Integer hotelId) {
        try (var em = emf.createEntityManager()) {
            var hotel = em.find(Hotel.class, hotelId);
            return hotel.getRooms().stream().anyMatch(r -> r.getRoomNumber().equals(roomNumber));
        }
    }

    */
/* denne er metode er uden at bruge streams
    public Boolean validateHotelRoomNumber(Integer roomNumber, Integer hotelId) {
        try (var em = emf.createEntityManager()) {
            var query = em.createQuery("SELECT r FROM Room r WHERE r.roomNumber = :roomNumber and r.hotel.id = :hotelId", Room.class);
            query.setParameter("roomNumber", roomNumber);
            query.setParameter("hotelId", hotelId);
            return !query.getResultList().isEmpty();
        }
    }
*//*


}
*/
