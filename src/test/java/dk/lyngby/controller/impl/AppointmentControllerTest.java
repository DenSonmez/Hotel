package dk.lyngby.controller.impl;

import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.config.HibernateConfig;


import dk.lyngby.dto.AppointmentDTO;
import dk.lyngby.dto.PatientDTO;
import dk.lyngby.model.Appointment;
import dk.lyngby.model.Patient;
import dk.lyngby.model.Role;

import dk.lyngby.model.User;
import io.javalin.Javalin;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManagerFactory;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


    class AppointmentControllerTest {

    /*    private static Javalin app;
        private static final String BASE_URL = "http://localhost:7777/api/v1";
        private static AppointmentController appointmentController;
        private static EntityManagerFactory emfTest;
        private static Object adminToken;
        private static Object userToken;
        private static User user, admin;
        private static Role userRole, adminRole;
        private static Appointment a1, a2;

        @BeforeAll
        static void beforeAll() {
            HibernateConfig.setTest(true);
            emfTest = HibernateConfig.getEntityManagerFactory();
            appointmentController = new AppointmentController(emfTest);
            app = Javalin.create();
            ApplicationConfig.startServer(app, 7777);
              // Create users and roles
            user = new User("usertest", "user123");
            admin = new User("admintest", "admin123");
            userRole = new Role("user");
            adminRole = new Role("admin");
            user.addRole(userRole);
            admin.addRole(adminRole);
            try (var em = emfTest.createEntityManager()) {
                em.getTransaction().begin();
                em.persist(userRole);
                em.persist(adminRole);
                em.persist(user);
                em.persist(admin);
                em.getTransaction().commit();
            }

            // Get tokens
            UserController userController = new UserController();
            adminToken = getToken(admin.getUsername(), "admin123");
            userToken = getToken(user.getUsername(), "user123");
        }


        @BeforeEach
        void setUp() {
            Set<Patient> patients1 = getPatients();
            Set<Patient> patients2 = getPatients2();


            try (var em = emfTest.createEntityManager()) {
                em.getTransaction().begin();
                em.createQuery("DELETE FROM Patient p").executeUpdate();
                em.createQuery("DELETE FROM Appointment a").executeUpdate();
                em.createNativeQuery("ALTER SEQUENCE patient_patient_id_seq RESTART WITH 1").executeUpdate();
                em.createNativeQuery("ALTER SEQUENCE appointment_appointment_id_seq RESTART WITH 1").executeUpdate();
                a1 = new Appointment(LocalDateTime.of(2023, 7, 27, 17, 30), "Tandrensning");
                a2 = new Appointment(LocalDateTime.of(2023, 7, 28, 18, 45), "Brækket ben");
                a1.setPatient(patients1);
                a2.setPatient(patients2);
                em.persist(a1);
                em.persist(a2);
                em.getTransaction().commit();
            }
        }

        @AfterAll
        static void tearDown() {
            HibernateConfig.setTest(false);
            ApplicationConfig.stopServer(app);
        }

        @Test
        void read() {
            given()
                    .header("Authorization", adminToken)
                    .contentType("application/json")
                    .when()
                    .get(BASE_URL + "/appointments/" + a1.getAppointmentId())
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.OK_200)
                    .body("id", equalTo(a1.getAppointmentId()));
        }

        @Test
        void readAll() {
            List<AppointmentDTO> appointmentDtoList =
                    given()
                            .contentType("application/json")
                            .when()
                            .get(BASE_URL + "/appointments")
                            .then()
                            .assertThat()
                            .statusCode(HttpStatus.OK_200)
                            .extract()
                            .body()
                            .jsonPath()
                            .getList("",AppointmentDTO.class);

            AppointmentDTO a1DTO = new AppointmentDTO(a1);
            AppointmentDTO a2DTO = new AppointmentDTO(a2);

            assertEquals(appointmentDtoList.size(), 2);
            assertThat(appointmentDtoList, containsInAnyOrder(a1DTO, a2DTO));
        }

        @Test
        void create() {
            Appointment a3 = new Appointment(LocalDateTime.of(2023, 9, 5, 11, 0), "Operation");
            Patient p1 = new Patient("Kiko", "Hamster", "Har haft ondt i tænderne tidligere");
          //  Patient p2 = new Patient("Bozo", "Dog", "Havde øjenbetændelse i 2 uger");
            a3.addPatients(p1);
           // a3.addPatients(p2);

            AppointmentDTO newAppointment = new AppointmentDTO(a3);

            List<PatientDTO> patientDtos =
                    given()
                            .header("Authorization", adminToken)
                            .contentType(ContentType.JSON)
                            .body(newAppointment)
                            .when()
                            .post(BASE_URL + "/appointments")
                            .then()
                            .statusCode(201)
                         .body("id", equalTo(3))
                            .body("appointmentDate", equalTo("2023-09-05T11:00:00"))
                            .body("reason", equalTo("Operation"))
                            .body("patients", hasSize(2))
                            .extract().body().jsonPath().getList("patients", PatientDTO.class);

            assertThat(patientDtos, containsInAnyOrder(new PatientDTO(p1)));
        }

        @Test
        void update() {
            AppointmentDTO updateAppointment = new AppointmentDTO(
                    LocalDateTime.of(2023, 9, 15, 9, 30),
                    "Øreinfektion");

            given()
                    .header("Authorization", adminToken)
                    .contentType(ContentType.JSON)
                    .body(updateAppointment)
                    .when()
                    .put(BASE_URL + "/appointments/" + a1.getAppointmentId()) // Tilføj appointmentId til stien
                    .then()
                    .statusCode(200)
                    .body("appointmentId", equalTo(a1.getAppointmentId()))
                    .body("appointmentDate", equalTo("2023-09-15T09:30:00"))
                    .body("reason", equalTo("Øreinfektion"))
                    .body("patients", hasSize(6));
        }

        @Test
        void delete() {
            given()
                    .header("Authorization", adminToken)
                    .contentType(ContentType.JSON)
                    .when()
                    .delete(BASE_URL + "/appointments/" + a1.getAppointmentId())
                    .then()
                    .statusCode(204);

            given()
                    .header("Authorization", adminToken)
                    .contentType(ContentType.JSON)
                    .when()
                    .get(BASE_URL + "/appointments/" + a1.getAppointmentId())
                    .then()
                    .statusCode(404);
        }

        @NotNull
        private static Set<Patient> getPatients() {
            return Set.of(
                    new Patient("Kiko", "Hamster", "Har haft ondt i tænderne i 3 tidligere"),
                    new Patient("Bozo", "Dog", "Havde øjenbetændelse i 2 uger"),
                    new Patient("Whiskers", "Cat", "Skader fra en slåskamp"),
                    new Patient("Buddy", "Dog", "Fødevareallergi"),
                    new Patient("Sasha", "Cat", "Hudproblemer"),
                    new Patient("Charlie", "Dog", "Hoste og nysen"),
                    new Patient("Luna", "Rabbit", "Øjenirritation"),
                    new Patient("Oscar", "Dog", "Skader fra leg i parken")
            );
        }

        @NotNull
        private static Set<Patient> getPatients2() {
            return Set.of(
                    new Patient("Sam", "Human", "Hospitalized for anxiety"),
                    new Patient("Norma", "Human", "Psychological assessment"),
                    new Patient("Dylan", "Human", "Recovering from gunshot wound"),
                    new Patient("Caleb", "Human", "Psychiatric evaluation"),
                    new Patient("Emma", "Human", "Respiratory issues"),
                    new Patient("Romero", "Human", "Critical condition")
            );
        }


        public static Object getToken(String username, String password) {
            return login(username, password);
        }

        private static Object login(String username, String password) {
            String json = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

            var token = given()
                    .contentType("application/json")
                    .body(json)
                    .when()
                    .post("http://localhost:7777/api/v1/auth/login")
                    .then()
                    .extract()
                    .response()
                    .body()
                    .path("token");

            return "Bearer " + token;
        }
*/
    }


