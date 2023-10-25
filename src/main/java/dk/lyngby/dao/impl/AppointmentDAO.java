package dk.lyngby.dao.impl;

import dk.lyngby.model.Appointment;

import jakarta.persistence.EntityManagerFactory;


public class AppointmentDAO extends GenericDAO<Appointment, Integer> {

    private static AppointmentDAO instance;

    public AppointmentDAO(Class<Appointment> entityClass, EntityManagerFactory emf) {
        super(entityClass, emf);
    }

    public static AppointmentDAO getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new AppointmentDAO(Appointment.class, emf);
        }
        return instance;
    }



  /*  public Appointment addPatientToAppointment(Integer appointmentId, Patient patient) {
        return addRelatedEntity(appointmentId, "patients", patient);
    }
*/
}


/*
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HotelDAO implements IDAO<Hotel, Integer> {

    private static HotelDAO instance;
    private static EntityManagerFactory emf;

    public static HotelDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new HotelDAO();
        }
        return instance;
    }

    @Override
    public Hotel read(Integer integer) {
        try (var em = emf.createEntityManager()) {
            return em.find(Hotel.class, integer);
        }
    }

    @Override
    public List<Hotel> readAll() {
        try (var em = emf.createEntityManager()) {
            var query = em.createQuery("SELECT h FROM Hotel h", Hotel.class);
            return query.getResultList();
        }
    }

    @Override
    public Hotel create(Hotel hotel) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(hotel);
            em.getTransaction().commit();
            return hotel;
        }
    }

    @Override
    public Hotel update(Integer integer, Hotel hotel) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            var h = em.find(Hotel.class, integer);
            h.setHotelName(hotel.getHotelName());
            h.setHotelAddress(hotel.getHotelAddress());
            h.setHotelType(hotel.getHotelType());
            Hotel merge = em.merge(h);
            em.getTransaction().commit();
            return merge;
        }
    }

    @Override
    public void delete(Integer integer) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            var hotel = em.find(Hotel.class, integer);
            em.remove(hotel);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (var em = emf.createEntityManager()) {
            var hotel = em.find(Hotel.class, integer);
            return hotel != null;
        }
    }
}
*/
