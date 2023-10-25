package dk.lyngby.controller.impl;

import dk.lyngby.dao.impl.AppointmentDAO;

import dk.lyngby.dto.AppointmentDTO;

import dk.lyngby.model.Appointment;

import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentController extends BaseController<Appointment, Integer> {

    public AppointmentController(EntityManagerFactory emf) {
        super(AppointmentDAO.getInstance(emf), Appointment.class);
    }


    @Override
    public boolean validatePrimaryKey(Integer key) {
        return dao.validatePrimaryKey(key);
    }


    @Override
    public Appointment validateEntity(Context ctx) {
        return ctx.bodyValidator(Appointment.class)
                .check(a -> a.getReason() != null && !a.getReason().isEmpty(), "Hotel address must be set")
                .check(a -> a.getAppointmentDate() != null && a.getAppointmentDate().isAfter(LocalDateTime.now()), "Appointment date must be in the future")
                .get();
    }


    @Override
    protected Object convertToDTO(Appointment entity) {
        return new AppointmentDTO(entity);
    }

    @Override
    protected List<Object> convertToDTOList(List<Appointment> entities) {
        return entities.stream().map(AppointmentDTO::new).collect(Collectors.toList());
    }

    @Override
    protected Class<Integer> getIDClass() {
        return Integer.class;
    }

}




/*
public class HotelController implements IController<Hotel, Integer> {

    private final HotelDAO dao;

    public HotelController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = HotelDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        // request
        // Hent det unikke ID fra URL-parametrene og valider det
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // entity
        //brug datalaget (typisk en DAO-klasse) til at læse hoteloplysninger fra din datakilde ved hjælp af det unikke ID.
        Hotel hotel = dao.read(id);
        // dto
        // Konverter det opdaterede hotelobjekt til et DTO (Data Transfer Object)
        HotelDTO hotelDto = new dk.lyngby.dto.HotelDTO(hotel);
        // response
        ctx.res().setStatus(200);
        // Send hotelDTO som JSON-respons
        ctx.json(hotelDto, HotelDTO.class);
    }

    @Override
    public void readAll(Context ctx) {
        // entity
        List<Hotel> hotels = dao.readAll();
        // dto
        List<HotelDTO> hotelDtos = HotelDTO.toHotelDTOList(hotels);
        // response
        ctx.res().setStatus(200);
        ctx.json(hotelDtos, dk.lyngby.dto.HotelDTO.class);
    }

    @Override
    public void create(Context ctx) {
        // request
        // her laver vi et hotel objekt ud fra det json objekt vi får fra requesten
        Hotel jsonRequest = ctx.bodyAsClass(Hotel.class);
        // entity
        // her tilføjer vi hotellet til databasen
        Hotel hotel = dao.create(jsonRequest);
        // dto
        // her(Konverter)laver vi et hotelDto objekt ud fra det hotel objekt vi lige har tilføjet til databasen
        HotelDTO hotelDto = new HotelDTO(hotel);
        // response
        ctx.res().setStatus(201);
        ctx.json(hotelDto, dk.lyngby.dto.HotelDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // request
        // Her bliver der lavet et nyt hotel objekt som bliver valideret
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();

        // entity
        // Her bliver det hotel objekt, som vi har lavet i requesten, opdateret
        Hotel update = dao.update(id, validateEntity(ctx), Hotel.class); // Tilføj entitetstypen (Hotel.class)

        // dto
        // Konverter det opdaterede hotel til et DTO (Data Transfer Object)
        HotelDTO hotelDto = new HotelDTO(update);

        // response
        ctx.res().setStatus(200);
        ctx.json(hotelDto, Hotel.class);
    }

    @Override
    public void delete(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // entity
        dao.delete(id);
        // response
        ctx.res().setStatus(204);
    }

    @Override
    //her tjekker vi om det er et valid hotel objekt
    public boolean validatePrimaryKey(Integer integer) {
        return dao.validatePrimaryKey(integer);
    }

    @Override
    //her tjekker vi om det er et valid hotel objekt
    public Hotel validateEntity(Context ctx) {
        return ctx.bodyValidator(Hotel.class)
                .check(h -> h.getHotelAddress() != null && !h.getHotelAddress().isEmpty(), "Hotel address must be set")
                .check(h -> h.getHotelName() != null && !h.getHotelName().isEmpty(), "Hotel name must be set")
                .check(h -> h.getHotelType() != null, "Hotel type must be set")
                .get();
    }

}
*/

