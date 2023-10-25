package dk.lyngby;

import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.controller.impl.ExceptionController;
import dk.lyngby.exception.ApiException;
import dk.lyngby.exception.Message;
import dk.lyngby.routes.Routes;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        ApplicationConfig
                .startServer(
                        Javalin.create(),
                        Integer.parseInt(ApplicationConfig.getProperty("javalin.port")));
    }
}
