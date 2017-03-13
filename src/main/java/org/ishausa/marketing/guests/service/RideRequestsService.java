package org.ishausa.marketing.guests.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.ishausa.marketing.guests.model.RideRequest;
import org.ishausa.marketing.guests.model.User;
import org.mongodb.morphia.Datastore;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Prasanna Venkat on 1/7/2017.
 */
public class RideRequestsService {
    private static final Logger log = Logger.getLogger(RideRequestsService.class.getName());

    private static final Gson GSON = new GsonBuilder().create();

    private final Datastore datastore;
    private final RideOfferAndRequestAdapter rideOfferAndRequestAdapter;

    public RideRequestsService(final Datastore datastore,
                               final UsersService usersService,
                               final OfferRequestMatchesService offerRequestMatchesService) {
        this.datastore = datastore;
        this.rideOfferAndRequestAdapter = new RideOfferAndRequestAdapter(usersService, offerRequestMatchesService);
    }

    public void createRideRequest(final User requester, final String tripId, final String jsonBody) {
        final JsonObject rideRequest = GSON.fromJson(jsonBody, JsonElement.class).getAsJsonObject();
        final int seatsRequested = rideRequest.get("seats").getAsInt();

        final RideRequest rideRequestModel =
                new RideRequest(tripId, requester.getUserId(), seatsRequested, new Date());

        datastore.save(rideRequestModel);

        log.info("Created ride request from jsonBody: " + jsonBody + " for user: " + requester + ", as: " + rideRequestModel);
    }

    public List<RideOfferAndRequestAdapter.RideOfferOrRequest> findForTrip(final String tripId) {
        return datastore.createQuery(RideRequest.class)
                .filter("tripId =", tripId)
                .asList()
                .stream()
                .map(rideOfferAndRequestAdapter::transform)
                .collect(Collectors.toList());
    }

    public RideOfferAndRequestAdapter.RideOfferOrRequest findForTripAndUser(final String tripId, final String userId) {
        final RideRequest rideRequest = datastore.createQuery(RideRequest.class)
                .filter("tripId =", tripId)
                .filter("userId =", userId)
                .get();
        log.info("RideRequest fetched for tripId: " + tripId + " and userId:" + userId + ", is: " + rideRequest);

        return rideOfferAndRequestAdapter.transform(rideRequest);
    }
}
