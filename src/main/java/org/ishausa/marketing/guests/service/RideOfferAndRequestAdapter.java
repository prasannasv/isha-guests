package org.ishausa.marketing.guests.service;

import lombok.Data;
import org.ishausa.marketing.guests.model.OfferRequestMatch;
import org.ishausa.marketing.guests.model.User;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Converts the RideOfferOrRequest & RideRequest instance to an object that is view friendly.
 *
 * Created by Prasanna Venkat on 1/2/2017.
 */
public class RideOfferAndRequestAdapter {
    private static final Logger log = Logger.getLogger(RideOfferAndRequestAdapter.class.getName());

    private final UsersService usersService;
    private final OfferRequestMatchesService offerRequestMatchesService;

    public RideOfferAndRequestAdapter(final UsersService usersService,
                                      final OfferRequestMatchesService offerRequestMatchesService) {
        this.usersService = usersService;
        this.offerRequestMatchesService = offerRequestMatchesService;
    }

    public RideOfferOrRequest transform(final org.ishausa.marketing.guests.model.RideOffer rideOffer) {
        if (rideOffer == null) {
            return null;
        }
        log.info("Transforming rideOffer: " + rideOffer);

        final User user = usersService.findById(rideOffer.getUserId());
        log.info("Found user for userId: " + rideOffer.getUserId() + ", user: " + user);

        final RideOfferOrRequest adaptedOffer = new RideOfferOrRequest();

        adaptedOffer.setUser(String.format("%s (%s)", user.getName(), user.getEmail()));
        adaptedOffer.setDate(rideOffer.getOfferedOn());
        adaptedOffer.setSeats(rideOffer.getSeatsOffered());

        final List<OfferRequestMatch> matchedRequests =
                offerRequestMatchesService.findMatchedOffersForTripAndOffer(rideOffer.getTripId(), rideOffer.getId());
        log.info("Found matched requests as: " + matchedRequests);

        final int seatsMatched = matchedRequests.stream().mapToInt(OfferRequestMatch::getSeatsMatched).sum();
        log.info("Seats matched: " + seatsMatched);

        adaptedOffer.setSeatsMatched(seatsMatched);

        return adaptedOffer;
    }

    public RideOfferOrRequest transform(final org.ishausa.marketing.guests.model.RideRequest rideRequest) {
        if (rideRequest == null) {
            return null;
        }
        log.info("Transforming rideRequest: " + rideRequest);

        final User user = usersService.findById(rideRequest.getUserId());
        log.info("Found user for userId: " + rideRequest.getUserId() + ", user: " + user);

        final RideOfferOrRequest adaptedOffer = new RideOfferOrRequest();

        adaptedOffer.setUser(String.format("%s (%s)", user.getName(), user.getEmail()));
        adaptedOffer.setDate(rideRequest.getRequestedOn());
        adaptedOffer.setSeats(rideRequest.getSeatsRequested());

        final List<OfferRequestMatch> matchedRequests =
                offerRequestMatchesService.findMatchedOffersForTripAndRequest(
                        rideRequest.getTripId(), rideRequest.getId());
        log.info("Found matched requests as: " + matchedRequests);

        final int seatsMatched = matchedRequests.stream().mapToInt(OfferRequestMatch::getSeatsMatched).sum();
        log.info("Seats matched: " + seatsMatched);

        adaptedOffer.setSeatsMatched(seatsMatched);

        return adaptedOffer;
    }

    @Data
    public static class RideOfferOrRequest {
        String user;
        int seats;
        Date date;
        int seatsMatched;
    }
}
