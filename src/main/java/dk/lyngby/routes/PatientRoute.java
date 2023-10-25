package dk.lyngby.routes;


import dk.lyngby.controller.impl.PatientController;

import dk.lyngby.security.RouteRoles;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PatientRoute {
    private final PatientController patientController;

    public PatientRoute(EntityManagerFactory emf) {
        this.patientController = new PatientController(emf);
    }

    protected EndpointGroup getRoutes() {

        return () -> {
            path("/patient", () -> {
                post("/appointment/{id}", patientController::create, RouteRoles.ADMIN, RouteRoles.MANAGER);
                get("/", patientController::readAll, RouteRoles.ANYONE);
                get("/{id}", patientController::read, RouteRoles.ADMIN, RouteRoles.MANAGER);
                put("/{id}", patientController::update, RouteRoles.ADMIN, RouteRoles.MANAGER);
                delete("/{id}", patientController::delete, RouteRoles.ADMIN, RouteRoles.MANAGER);
            });
        };
    }
}