package dk.lyngby.routes;


import dk.lyngby.controller.impl.AppointmentController;
import dk.lyngby.security.RouteRoles;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AppointmentRoute {


        private final AppointmentController appointmentController;

        public AppointmentRoute(EntityManagerFactory emf) {
            this.appointmentController = new AppointmentController(emf);
        }


    protected EndpointGroup getRoutes() {

        return () -> {
            path("/appointments", () -> {
                // hvis vi feks vælge at create en hotel så er man nød til at være manager eller admin
                post("/", appointmentController::create, RouteRoles.ADMIN, RouteRoles.MANAGER);
                get("/", appointmentController::readAll, RouteRoles.ANYONE);
                get("/{id}", appointmentController::read, RouteRoles.USER, RouteRoles.ADMIN, RouteRoles.MANAGER);
                put("/{id}", appointmentController::update, RouteRoles.ADMIN, RouteRoles.MANAGER);
                delete("/{id}", appointmentController::delete, RouteRoles.ADMIN, RouteRoles.MANAGER);
            });
        };
    }
}