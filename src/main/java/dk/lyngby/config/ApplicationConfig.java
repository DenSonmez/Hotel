package dk.lyngby.config;

import dk.lyngby.controller.impl.AccessManagerController;
import dk.lyngby.routes.Routes;
import dk.lyngby.security.RouteRoles;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.plugin.bundled.RouteOverviewPlugin;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ApplicationConfig {


    private static final AccessManagerController ACCESS_MANAGER_HANDLER = new AccessManagerController();
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    private static void configuration(JavalinConfig config) {
        config.routing.contextPath = "/api/v1"; // base path for all routes
        config.http.defaultContentType = "application/json"; // default content type for requests
        config.plugins.register(new RouteOverviewPlugin("/", RouteRoles.ANYONE)); // enables route overview at /
        config.accessManager(ACCESS_MANAGER_HANDLER::accessManagerHandler);
    }

    public static void startServer(Javalin app, int port) {
        EntityManagerFactory appo = HibernateConfig.getEntityManagerFactory(); // Få EntityManagerFactory for hoteller
        EntityManagerFactory pati = HibernateConfig.getEntityManagerFactory(); // Få EntityManagerFactory for værelser
        Routes routes = new Routes(appo, pati);
        app.updateConfig(ApplicationConfig::configuration);
        app.routes(routes.getRoutes(app));
        HibernateConfig.setTest(false);
        app.start(port);
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }

    public static String getProperty(String propName) throws IOException {
        try (InputStream is = HibernateConfig.class.getClassLoader().getResourceAsStream("properties-from-pom.properties")) {
            Properties prop = new Properties();
            prop.load(is);
            return prop.getProperty(propName);
        } catch (IOException ex) {
            LOGGER.error("Could not read property from pom file. Build Maven!");
            throw new IOException("Could not read property from pom file. Build Maven!");
        }
    }
}