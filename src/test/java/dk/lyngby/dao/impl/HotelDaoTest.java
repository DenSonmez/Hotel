package dk.lyngby.dao.impl;

import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.config.HibernateConfig;


import io.javalin.Javalin;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManagerFactory;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HotelDaoTest {
 /*   //private static Javalin app; //er en instans af Javalin, der bruges til at oprette en HTTP-server.
    private static final String BASE_URL = "http://localhost:7777/api/v1"; //er en streng, der indeholder grundlæggende URL'en for API'en.
    private static HotelDAO hotelDao;
    private static HotelController hotelController;
    private static EntityManagerFactory emfTest;

    private static Hotel h1, h2; //er to instanser af Hotel, der bruges til at teste med.

    @BeforeAll
    static void beforeAll()
    {
        HibernateConfig.setTest(true);
        emfTest = HibernateConfig.getEntityManagerFactory();
        hotelDao = HotelDAO.getInstance(emfTest);
    }


    @BeforeEach
        //. Denne setUp-metode forbereder testmiljøet ved at slette eksisterende data,
        // nulstille sekvensgeneratorene og indsætte testdata i databasen, så hver testmetode kan køre med en kendt og ensartet tilstand
    void setUp() {
        //henter sæt af værelser fra to forskellige kilde som vi angivet i getCalRooms() og getBatesRooms() metoder længere ned i kode
        Set<Room> calRooms = getCalRooms();
        Set<Room> hilRooms = getBatesRooms();

        try (var em = emfTest.createEntityManager()) {
            em.getTransaction().begin();
            // Delete all rows
            //sletter alle rækker fra tabellerne Room og Hotel ved at køre SQL-sletningsforespørgsler.
            em.createQuery("DELETE FROM Room r").executeUpdate();
            em.createQuery("DELETE FROM Hotel h").executeUpdate();
            // Reset sequence,  nulstiller sekvensgeneratorene til rækkeidentifikation for værelser og hoteller.
            em.createNativeQuery("ALTER SEQUENCE room_room_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE hotel_hotel_id_seq RESTART WITH 1").executeUpdate();
            // Insert test data
            //indsætter testdata i tabellerne Room og Hotel ved at oprette instanser af Hotel og Room og kalde persist() metoden på dem.
            h1 = new Hotel("Hotel California", "California", Hotel.HotelType.LUXURY);
            h2 = new Hotel("Bates Motel", "Lyngby", Hotel.HotelType.STANDARD);
            h1.setRooms(calRooms);
            h2.setRooms(hilRooms);
            em.persist(h1);
            em.persist(h2);
            em.getTransaction().commit();


        }
    }

    @AfterAll
    static void tearDown() {
        HibernateConfig.setTest(false);
        //ApplicationConfig.stopServer(app);
    }

    @Test
    void read() {
        Hotel hotelActually = hotelDao.read(h1.getId());
        assertEquals(h1, hotelActually);
    }

    @Test
    void readAll() {
        List<Hotel> hotelListExpected = new ArrayList<>(List.of(h1,h2));

        List<Hotel> hotelListActually = hotelDao.readAll();

        assertEquals(hotelListExpected, hotelListActually);
    }

    @Test
    void create() {
        // Hotel(String hotelName, String hotelAddress, HotelType hotelType)
        Hotel expected = new Hotel("The Expected Hotel",
                "The Expected street 2",
                Hotel.HotelType.LUXURY);

        Set<Room> roomSet = getCalRooms();
        expected.setRooms(roomSet);

        expected = hotelDao.create(expected);

        Hotel actual = hotelDao.read(expected.getId());

        assertEquals(expected, actual);
    }

   @ Test
    void update() {
        Hotel expected = hotelDao.read(h1.getId());
        expected.setHotelName("The Expected Hotel");
        expected = hotelDao.update(h1.getId(), expected, Hotel.class); // Tilføj entitetstypen (Hotel.class)

        Hotel actual = hotelDao.read(h1.getId());

        assertEquals(expected, actual);
    }

    @Test
    void delete() {
        int id = h1.getId();
        hotelDao.delete(h1.getId());

        Hotel h = hotelDao.read(id);

        assertNull(h);
    }



    @NotNull
    private static Set<Room> getCalRooms() {
        return Set.of(
                new Room(100, new BigDecimal(2520), Room.RoomType.SINGLE, 50),
                new Room(101, new BigDecimal(2520), Room.RoomType.SINGLE, 50),
                new Room(102, new BigDecimal(2520), Room.RoomType.SINGLE, 50),
                new Room(103, new BigDecimal(2520), Room.RoomType.SINGLE, 50),
                new Room(104, new BigDecimal(3200), Room.RoomType.DOUBLE, 100),
                new Room(105, new BigDecimal(4500), Room.RoomType.SUITE, 150)
        );
    }

    @NotNull
    private static Set<Room> getBatesRooms() {
        return Set.of(
                new Room(111, new BigDecimal(2520), Room.RoomType.SINGLE, 50),
                new Room(112, new BigDecimal(2520), Room.RoomType.SINGLE, 50),
                new Room(113, new BigDecimal(2520), Room.RoomType.SINGLE, 50),
                new Room(114, new BigDecimal(2520), Room.RoomType.DOUBLE, 100),
                new Room(115, new BigDecimal(3200), Room.RoomType.DOUBLE, 100),
                new Room(116, new BigDecimal(4500), Room.RoomType.SUITE, 150)
        );
    }*/
}
