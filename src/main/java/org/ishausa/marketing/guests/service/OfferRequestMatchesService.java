package org.ishausa.marketing.guests.service;

import org.ishausa.marketing.guests.model.OfferRequestMatch;
import org.mongodb.morphia.Datastore;

import java.util.List;

/**
 * Created by Prasanna Venkat on 1/2/2017.
 */
public class OfferRequestMatchesService {
    private final Datastore datastore;

    public OfferRequestMatchesService(final Datastore datastore) {
        this.datastore = datastore;
    }

    public List<OfferRequestMatch> findMatchedOffersForTripAndOffer(final String tripId, final String offerId) {
        return datastore.createQuery(OfferRequestMatch.class)
                .filter("tripId =", tripId)
                .filter("offerId =", offerId)
                .asList();
    }

    public List<OfferRequestMatch> findMatchedOffersForTripAndRequest(final String tripId, final String requestId) {
        return datastore.createQuery(OfferRequestMatch.class)
                .filter("tripId =", tripId)
                .filter("requestId =", requestId)
                .asList();
    }
}
