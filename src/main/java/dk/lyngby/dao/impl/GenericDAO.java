package dk.lyngby.dao.impl;
import dk.lyngby.dao.IDAO;
import dk.lyngby.model.Appointment;
import dk.lyngby.model.Patient;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;


public abstract class GenericDAO<T, K> implements IDAO<T, K> {
    private final Class<T> entityClass;
    protected static EntityManagerFactory emf;

    public GenericDAO(Class<T> entityClass, EntityManagerFactory emf) {
        this.entityClass = entityClass;
        GenericDAO.emf = emf;
    }

    @Override
    public T read(K key) {
        try (var em = emf.createEntityManager()) {
            return em.find(entityClass, key);
        }
    }

    @Override
    public List<T> readAll() {
        try (var em = emf.createEntityManager()) {
            var query = em.createQuery("SELECT t FROM " + entityClass.getSimpleName() + " t", entityClass);
            return query.getResultList();
        }
    }

    @Override
    public T create(T entity) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        }
    }

    @Override
    public <T> T update(Integer id, T entity, Class<T> entityType) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            T managedEntity = em.find(entityType, id);
            if (managedEntity != null) {

                if (entity instanceof Appointment appointment && managedEntity instanceof Appointment managedAppointment) {
                    managedAppointment.setReason(appointment.getReason());
                    managedAppointment.setAppointmentDate(appointment.getAppointmentDate());
                } else if (entity instanceof Patient patient && managedEntity instanceof Patient managedPatient) {
                    managedPatient.setName(patient.getName());
                    managedPatient.setSpecies(patient.getSpecies());
                    managedPatient.setMedicalHistory(patient.getMedicalHistory());
                }

                T mergedEntity = em.merge(managedEntity);
                em.getTransaction().commit();
                return mergedEntity;
            }
            return null;
        }
    }


    /*@Override
    public T update(K key, T entity) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            T oldEntity = em.find(entityClass, key);
            em.merge(entity);
            em.getTransaction().commit();
            return oldEntity;
        }
    }*/

    @Override
    public boolean delete(K key) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            T entity = em.find(entityClass, key);
            em.remove(entity);
            em.getTransaction().commit();
        }
        return false;
    }

    @Override
    public boolean validatePrimaryKey(K key) {
        try (var em = emf.createEntityManager()) {
            return em.find(entityClass, key) != null;
        }
    }
}
