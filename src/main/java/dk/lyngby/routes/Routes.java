package dk.lyngby.routes;

import dk.lyngby.controller.impl.ExceptionController;
import dk.lyngby.exception.ApiException;
import dk.lyngby.exception.AuthorizationException;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes { // denne classe bruges til at håndtere alle routes dvs alle endpoints

    private final ExceptionController exceptionController = new ExceptionController();
    private int count = 0;

    private final AppointmentRoute appointmentRoute;
    private final PatientRoute patientRoute;

    public Routes(EntityManagerFactory appointment, EntityManagerFactory patient) {
        appointmentRoute = new AppointmentRoute(appointment);
        patientRoute = new PatientRoute(patient);
    }

    private final UserRoutes userRoutes = new UserRoutes();

    private final Logger LOGGER = LoggerFactory.getLogger(Routes.class);

    private void requestInfoHandler(Context ctx) {
        String requestInfo = ctx.req().getMethod() + " " + ctx.req().getRequestURI();
        ctx.attribute("requestInfo", requestInfo);
    }

    public EndpointGroup getRoutes(Javalin app) {
        return () -> {
            app.before(this::requestInfoHandler);

            app.routes(() -> {
                path("/", userRoutes.getRoutes());
                path("/", appointmentRoute.getRoutes());
                path("/", patientRoute.getRoutes());
            });

            app.after(ctx -> LOGGER.info(" Request {} - {} was handled with status code {}", count++, ctx.attribute("requestInfo"), ctx.status()));

            app.exception(ConstraintViolationException.class, exceptionController::constraintViolationExceptionHandler);
            app.exception(ValidationException.class, exceptionController::validationExceptionHandler);
            app.exception(ApiException.class, exceptionController::apiExceptionHandler);
            app.exception(AuthorizationException.class, exceptionController::exceptionHandlerNotAuthorized);
            app.exception(Exception.class, exceptionController::exceptionHandler);

        };
    }
}