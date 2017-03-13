package org.ishausa.marketing.guests.app;

import com.google.common.collect.ImmutableMap;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.ishausa.marketing.guests.model.Role;
import org.ishausa.marketing.guests.model.User;
import org.ishausa.marketing.guests.renderer.JsonTransformer;
import org.ishausa.marketing.guests.renderer.SoyRenderer;
import org.ishausa.marketing.guests.security.AuthenticationHandler;
import org.ishausa.marketing.guests.security.HttpsEnforcer;
import org.ishausa.marketing.guests.service.OfferRequestMatchesService;
import org.ishausa.marketing.guests.service.RideOffersService;
import org.ishausa.marketing.guests.service.RideRequestsService;
import org.ishausa.marketing.guests.service.TripListResponse;
import org.ishausa.marketing.guests.service.EventsService;
import org.ishausa.marketing.guests.service.UsersService;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import spark.Spark;
import spark.utils.StringUtils;

import javax.servlet.http.HttpServletResponse;
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
public class EventGuestApp {
    private static final Logger log = Logger.getLogger(EventGuestApp.class.getName());

    private final JsonTransformer jsonTransformer;

    private final EventsService eventsService;
    private final UsersService usersService;
    private final RideOffersService rideOffersService;
    private final RideRequestsService rideRequestsService;
    private final AuthenticationHandler authenticationHandler;
    private final OfferRequestMatchesService offerRequestMatchesService;

    public static void main(final String[] args) {
        final EventGuestApp app = new EventGuestApp();

        app.init();
    }

    private EventGuestApp() {
        jsonTransformer = new JsonTransformer();
        final Datastore datastore = setupMorphia();
        eventsService = new EventsService(datastore);
        usersService = new UsersService(datastore);
        authenticationHandler = new AuthenticationHandler(usersService);
        offerRequestMatchesService = new OfferRequestMatchesService(datastore);
        rideOffersService = new RideOffersService(datastore, usersService, offerRequestMatchesService);
        rideRequestsService = new RideRequestsService(datastore, usersService, offerRequestMatchesService);
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

        configureTripResourceEndpoints();

        configureRideResourceEndpoints();
    }

    private void configureAuthEndpoints() {
        get(Paths.LOGIN, (req, res) -> SoyRenderer.INSTANCE.render(SoyRenderer.EventGuestsAppTemplate.LOGIN));
        post(Paths.VALIDATE_ID_TOKEN, authenticationHandler::finishLogin);
    }

    private void configureTripResourceEndpoints() {
        Spark.post(Paths.EVENTS, ContentType.JSON, (req, res) -> {
            final User authenticatedUser = req.attribute(AuthenticationHandler.AUTHENTICATED_USER);
            if (Role.REGULAR.equals(authenticatedUser.getRole())) {
                res.status(HttpServletResponse.SC_FORBIDDEN);
                return null;
            } else {
                return eventsService.createEvent(authenticatedUser, req.body());
            }
        }, jsonTransformer);

        get(Paths.EVENTS, ContentType.JSON, (req, res) ->
                new TripListResponse(req.attribute(AuthenticationHandler.AUTHENTICATED_USER),
                        eventsService.listAll()),
                jsonTransformer);

        get(Paths.EVENT_BY_ID, ContentType.JSON,
                (req, res) -> eventsService.find(req.params(Paths.ID_PARAM)), jsonTransformer);
    }

    private void configureRideResourceEndpoints() {
        post(Paths.RIDE_OFFER_FOR_TRIP_ID, ContentType.JSON, (req, res) -> {
            final User authenticatedUser = req.attribute(AuthenticationHandler.AUTHENTICATED_USER);
            final String tripId = req.params(Paths.ID_PARAM);

            rideOffersService.createRideOffer(authenticatedUser, tripId, req.body());

            return res;
        });

        get(Paths.RIDE_OFFERS_FOR_TRIP_ID, ContentType.JSON, (req, res) -> {
            final User user = req.attribute(AuthenticationHandler.AUTHENTICATED_USER);
            final String tripId = req.params(Paths.ID_PARAM);

            return user.getRole() == Role.REGULAR ? rideOffersService.findForTripAndUser(tripId, user.getUserId()) :
                    rideOffersService.findForTrip(tripId);
        }, jsonTransformer);

        post(Paths.RIDE_REQUEST_FOR_TRIP_ID, ContentType.JSON, (req, res) -> {
            final User authenticatedUser = req.attribute(AuthenticationHandler.AUTHENTICATED_USER);
            final String tripId = req.params(Paths.ID_PARAM);

            rideRequestsService.createRideRequest(authenticatedUser, tripId, req.body());

            return res;
        });

        get(Paths.RIDE_REQUESTS_FOR_TRIP_ID, ContentType.JSON, (req, res) -> {
            final User user = req.attribute(AuthenticationHandler.AUTHENTICATED_USER);
            final String tripId = req.params(Paths.ID_PARAM);

            return user.getRole() == Role.REGULAR ? rideRequestsService.findForTripAndUser(tripId, user.getUserId()) :
                    rideRequestsService.findForTrip(tripId);
        }, jsonTransformer);
    }
}
