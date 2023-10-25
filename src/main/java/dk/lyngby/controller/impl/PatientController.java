package dk.lyngby.controller.impl;


import dk.lyngby.dao.impl.PatientDAO;
import dk.lyngby.dto.AppointmentDTO;
import dk.lyngby.dto.PatientDTO;
import dk.lyngby.exception.Message;
import dk.lyngby.model.Appointment;
import dk.lyngby.model.Patient;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


public class PatientController extends BaseController<Patient, Integer> {

    private PatientDAO dao;

    public PatientController(EntityManagerFactory emf) {
        super(PatientDAO.getInstance(emf), Patient.class);
        this.dao = PatientDAO.getInstance(emf);
    }


    public void create(Context ctx) {

        Patient jsonRequest = validateEntity(ctx);

        int appointmentId = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();

        Boolean hasPatient = validateAppointmentTime.apply(jsonRequest.getPatientId(), appointmentId);

        if (hasPatient) {
            ctx.res().setStatus(400);
            ctx.json(new Message(400, "Patient id already in use by"));
            return;
        }
        Appointment appointment = dao.addPatientToAppointment(appointmentId, jsonRequest);

        AppointmentDTO appointmentDto = new AppointmentDTO(appointment);
        // response
        ctx.res().setStatus(201);
        // Send hotelDTO som JSON-respons
        ctx.json(appointmentDto, AppointmentDTO.class);
    }

    BiFunction<Integer, Integer, Boolean> validateAppointmentTime = (patientId, appointmentId) -> dao.validateAppointmentTime(patientId, appointmentId);

    @Override
    public boolean validatePrimaryKey(Integer key) {
        if (dao != null) {
            return dao.validatePrimaryKey(key);
        }
        return false;
    }


    @Override
    public Patient validateEntity(Context ctx) {
        return ctx.bodyValidator(Patient.class)
                .check(p -> p.getName() != null && !p.getName().isEmpty(), "Patient name must be set")
                .check(p -> p.getSpecies() != null && !p.getSpecies().isEmpty(), "Patient species must be set")
                .check(p -> p.getMedicalHistory() != null && !p.getMedicalHistory().isEmpty(), "Patient medical history must be set")
                .get();

    }

    @Override
    protected Object convertToDTO(Patient entity) {
        return new PatientDTO(entity);
    }

    @Override
    protected List<Object> convertToDTOList(List<Patient> entities) {
        return entities.stream().map(PatientDTO::new).collect(Collectors.toList());
    }

    @Override
    protected Class<Integer> getIDClass() {
        return Integer.class;
    }
}



/*
public class RoomController implements IController<Room, Integer> {

    private RoomDAO dao;

    public RoomController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = RoomDAO.getInstance(emf);
    }

    @Override
    //den her metode er for at hente et enkelt rum og returnere det som et json objekt
    public void read(Context ctx) {
        // request
        // Hent det unikke ID fra URL-parametrene og valider det
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // entity
        // Hvis hotellet allerede har et værelse med samme nummer, send en fejlrespons og afslut
        Room room = dao.read(id);
        // dto
        // Konverter det opdaterede roomobjekt til et DTO (Data Transfer Object)
        RoomDTO roomDto = new RoomDTO(room);
        // response
        ctx.res().setStatus(200);
        // Send roomDTO som JSON-respons
        ctx.json(roomDto, RoomDTO.class);

    }



    @Override
    public void readAll(Context ctx) {
        // requests
        Integer priceFrom = ctx.queryParamAsClass("priceFrom", Integer.class).allowNullable().check(p -> p != null && p > 0, "Not a valid price").get();
        Integer priceTo = ctx.queryParamAsClass("priceTo", Integer.class).allowNullable().check(p -> p != null && p > 0, "Not a valid price").get();
        if (priceFrom != null && priceTo != null && priceFrom > priceTo) {
            ctx.res().setStatus(400);
            ctx.json(new Message(400, "Price from must be less than price to"));
            return;
        }
        if (priceFrom == null || priceTo == null) {
            // entity
            List<Room> rooms = dao.readAll();
            // dto
            List<RoomDTO> roomDtos = RoomDTO.toRoomDTOList(rooms);
            // response
            ctx.res().setStatus(200);
            ctx.json(roomDtos, RoomDTO.class);
        } else {
            // entity
            List<Room> rooms = dao.findAllByPrice(priceFrom, priceTo);
            // dto
            List<RoomDTO> roomDtos = RoomDTO.toRoomDTOList(rooms);
            // response
            ctx.res().setStatus(200);
            ctx.json(roomDtos, RoomDTO.class);
        }
    }
    @Override
    //den her metode er for at oprette et nyt rum og returnere det som et json objekt
    public void create(Context ctx) {
        //her bliver der lavet et nyt rum objekt som bliver valideret
        Room jsonRequest = validateEntity(ctx);
        // Hent og valider hotellets ID fra URL-parametrene
        int hotelId = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // Kontroller, om hotellet allerede har et værelse med samme nummer
        Boolean hasRoom = validateHotelRoomNumber.apply(jsonRequest.getRoomNumber(), hotelId);
        // Hvis hotellet allerede har et værelse med samme nummer, send en fejlrespons og afslut

        if (hasRoom) {
            ctx.res().setStatus(400);
            ctx.json(new Message(400, "Room number already in use by hotel"));
            return;
        }

        // Tilføj værelset til hotellet ved hjælp af DAO-laget og få den opdaterede hotelobjekt
        // entity
        Hotel hotel = dao.addRoomToHotel(hotelId, jsonRequest);
        // Konverter det opdaterede hotelobjekt til et DTO (Data Transfer Object)
        // dto
        HotelDTO hotelDto = new HotelDTO(hotel);
        // response
        ctx.res().setStatus(201);
        // Send hotelDTO som JSON-respons
        ctx.json(hotelDto, HotelDTO.class);
    }


    @Override
    public void update(Context ctx) {
        // request
        // Her bliver der lavet et nyt rum objekt som bliver valideret
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();

        // entity
        // Opdater værelset i datalaget ved hjælp af DAO-laget og det validerede anmodningsobjekt
        Room update = dao.update(id, validateEntity(ctx), Room.class); // Tilføj entitetstypen (Room.class)

        // dto
        // Konverter det opdaterede værelse til et DTO (Data Transfer Object)
        RoomDTO roomDto = new RoomDTO(update);

        // response
        ctx.res().setStatus(200);
        // Send værelses-DTO som JSON-respons
        ctx.json(roomDto, RoomDTO.class);
    }

    @Override
    public void delete(Context ctx) {
        // request
        // Hent det unikke ID fra URL-parametrene og valider det
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // entity
        // Slet værelset fra datalaget ved hjælp af DAO-laget
        dao.delete(id);
        // response
        ctx.res().setStatus(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        return dao.validatePrimaryKey(integer);
    }

    // Checks if the room number is already in use by the hotel
    BiFunction<Integer, Integer, Boolean> validateHotelRoomNumber = (roomNumber, hotelId) -> dao.validateHotelRoomNumber(roomNumber, hotelId);

    @Override
    //
    public Room validateEntity(Context ctx) {
        //her tjekker vi om det er et valid rum objekt
        return ctx.bodyValidator(Room.class)
                //og her tjekker vi om det er et valid rum nummer
                .check(r -> r.getRoomNumber() != null && r.getRoomNumber() > 0, "Not a valid room number")
                .check(r -> r.getRoomType() != null, "Not a valid room type")
                .check(r -> r.getRoomPrice() != null, "Not a valid price")
                .get();
    }
}
*/

