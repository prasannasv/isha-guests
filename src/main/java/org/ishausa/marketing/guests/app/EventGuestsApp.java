package org.ishausa.marketing.guests.app;

import com.google.common.collect.ImmutableMap;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.ishausa.marketing.guests.model.Event;
import org.ishausa.marketing.guests.model.Role;
import org.ishausa.marketing.guests.model.User;
import org.ishausa.marketing.guests.renderer.JsonTransformer;
import org.ishausa.marketing.guests.renderer.SoyRenderer;
import org.ishausa.marketing.guests.security.AuthenticationHandler;
import org.ishausa.marketing.guests.security.HttpsEnforcer;
import org.ishausa.marketing.guests.service.CentersService;
import org.ishausa.marketing.guests.service.EventListResponse;
import org.ishausa.marketing.guests.service.EventsService;
import org.ishausa.marketing.guests.service.GuestsService;
import org.ishausa.marketing.guests.service.UsersService;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import spark.utils.StringUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

/**
 * Created by Prasanna Venkat on 12/29/2016.
 */
public class EventGuestsApp {
    private static final Logger log = Logger.getLogger(EventGuestsApp.class.getName());

    private final JsonTransformer jsonTransformer;

    private final EventsService eventsService;
    private final CentersService centersService;
    private final GuestsService guestsService;
    private final UsersService usersService;
    private final AuthenticationHandler authenticationHandler;

    public static void main(final String[] args) {
        final EventGuestsApp app = new EventGuestsApp();

        app.init();
    }

    private EventGuestsApp() {
        jsonTransformer = new JsonTransformer();
        final Datastore datastore = setupMorphia();
        eventsService = new EventsService(datastore);
        centersService = new CentersService(datastore);
        guestsService = new GuestsService(datastore);
        usersService = new UsersService(datastore);
        authenticationHandler = new AuthenticationHandler(usersService);
    }

    private Datastore setupMorphia() {
        final Morphia morphia = new Morphia();
        morphia.mapPackage("org.ishausa.marketing.guests.model");

        final Datastore datastore;
        final String mongoUri = System.getenv("MONGODB_URI");
        if (!StringUtils.isBlank(mongoUri)) {
            final MongoClientURI clientURI = new MongoClientURI(mongoUri);
            datastore = morphia.createDatastore(new MongoClient(clientURI), clientURI.getDatabase());
        } else {
            datastore = morphia.createDatastore(new MongoClient(), "event-guests");
        }

        datastore.ensureIndexes();

        return datastore;
    }

    private void init() {
        exception(Exception.class, (exception, request, response) -> {
            log.log(Level.WARNING, "Exception occurred when serving request to " + request.pathInfo(), exception);
            throw new RuntimeException(exception);
        });

        final String port = System.getenv("PORT");
        if (!StringUtils.isBlank(port)) {
            port(Integer.parseInt(port));
        }

        staticFiles.location(Paths.STATIC);
        if (RuntimeEnvironment.isOnHeroku()) {
            staticFiles.externalLocation("/app/src/main/webapp");
        } else {
            staticFiles.externalLocation("/Users/tosri/Code/IshaGuests/src/main/webapp");
        }

        initFilters();

        configureRoutes();
    }

    private void initFilters() {
        before(new HttpsEnforcer());
        before(authenticationHandler::ensureAuthenticated);
    }

    private void configureRoutes() {
        configureAuthEndpoints();

        get(Paths.MAIN, (req, res) ->
                SoyRenderer.INSTANCE.render(SoyRenderer.EventGuestsAppTemplate.MAIN,
                        ImmutableMap.of("name", req.session().attribute(AuthenticationHandler.NAME))));

        configureEventResourceEndpoints();

        configureGuestResourceEndpoints();

        configureAdminEndpoints();
    }

    private void configureAdminEndpoints() {
        configureCenterResourceEndpoints();

        configureUserResourceEndpoints();
    }

    private void configureAuthEndpoints() {
        get(Paths.LOGIN, (req, res) -> SoyRenderer.INSTANCE.render(SoyRenderer.EventGuestsAppTemplate.LOGIN));
        post(Paths.VALIDATE_ID_TOKEN, authenticationHandler::finishLogin);
    }

    private void configureEventResourceEndpoints() {
        post(Paths.EVENTS, ContentType.JSON, (req, res) -> {
            final User authenticatedUser = req.attribute(AuthenticationHandler.AUTHENTICATED_USER);
            if (authenticatedUser.getRole().compareTo(Role.EVENT_ADMIN) >= 0) {
                return eventsService.createEvent(authenticatedUser, req.body());
            } else {
                res.status(HttpServletResponse.SC_FORBIDDEN);
                return "NOT_OK";
            }
        }, jsonTransformer);

        get(Paths.EVENTS, ContentType.JSON, (req, res) -> {
            final User user = req.attribute(AuthenticationHandler.AUTHENTICATED_USER);
            if (user.getRole().compareTo(Role.EVENT_ADMIN) >= 0) {
                return new EventListResponse(user, eventsService.listAll(user));
            } else {
                return new EventListResponse(user, new ArrayList<>());
            }
        }, jsonTransformer);

        get(Paths.EVENT_BY_ID, ContentType.JSON,
                (req, res) -> eventsService.find(req.params(Paths.ID_PARAM)), jsonTransformer);
    }

    private void configureGuestResourceEndpoints() {
        get(Paths.GUESTS_FOR_EVENT_ID, ContentType.JSON, (req, res) -> {
            final String eventId = req.params(Paths.ID_PARAM);
            return guestsService.findForEventId(eventId);
        }, jsonTransformer);

        post(Paths.GUEST_FOR_EVENT_ID, ContentType.JSON, (req, res) -> {
            final User user = req.attribute(AuthenticationHandler.AUTHENTICATED_USER);
            if (user.getRole().compareTo(Role.EVENT_ADMIN) >= 0) {
                final String eventId = req.params(Paths.ID_PARAM);
                // TODO: Address non-existent eventId
                guestsService.addGuest(eventsService.find(eventId), req.body());
                return "OK";
            } else {
                res.status(HttpServletResponse.SC_FORBIDDEN);
                return "NOT_OK";
            }
        });

        // For bulk upload
        post(Paths.GUESTS_FOR_EVENT_ID, ContentType.MULTIPART_FORM_DATA, (req, res) -> {
            final User user = req.attribute(AuthenticationHandler.AUTHENTICATED_USER);
            if (user.getRole().compareTo(Role.EVENT_ADMIN) < 0) {
                res.status(HttpServletResponse.SC_FORBIDDEN);
                return "NOT_OK";
            }

            final String eventId = req.params(Paths.ID_PARAM);
            final Event event = eventsService.find(eventId);

            //http://stackoverflow.com/questions/29373468/spark-java-how-to-handle-multipart-form-data-input
            final MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
            req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

            final Part uploadedFile = req.raw().getPart("file");
            try (final InputStream in = uploadedFile.getInputStream()) {
                guestsService.addGuests(event, in);
            }

            return "OK";
        });
    }

    private void configureCenterResourceEndpoints() {
        get(Paths.CENTERS, ContentType.JSON, (req, res) -> centersService.listAll(), jsonTransformer);

        post(Paths.CENTERS, ContentType.JSON, (req, res) -> {
            final User authenticatedUser = req.attribute(AuthenticationHandler.AUTHENTICATED_USER);
            if (Role.REGULAR.equals(authenticatedUser.getRole())) {
                res.status(HttpServletResponse.SC_FORBIDDEN);
            } else {
                centersService.createCenter(authenticatedUser, req.body());
            }
            return "OK";
        });
    }

    private void configureUserResourceEndpoints() {
        get(Paths.USERS, ContentType.JSON, (req, res) -> {
            final User user = req.attribute(AuthenticationHandler.AUTHENTICATED_USER);
            if (Role.ADMIN.equals(user.getRole())) {
                return usersService.listAll();
            } else {
                res.status(HttpServletResponse.SC_FORBIDDEN);
                return "NOT_OK";
            }
        }, jsonTransformer);
    }
}
