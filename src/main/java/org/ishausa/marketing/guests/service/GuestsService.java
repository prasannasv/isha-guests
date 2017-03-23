package org.ishausa.marketing.guests.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ishausa.marketing.guests.model.Guest;
import org.mongodb.morphia.Datastore;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Prasanna Venkat on 3/22/2017.
 */
public class GuestsService {
    private static final Logger log = Logger.getLogger(GuestsService.class.getName());

    private static final Gson GSON = new GsonBuilder().create();

    private final Datastore datastore;

    public GuestsService(final Datastore datastore) {
        this.datastore = datastore;
    }

    public List<Guest> findForEventId(final String eventId) {
        return datastore.createQuery(Guest.class)
                .filter("eventId =", eventId)
                .asList();
    }

    public void addGuest(final String eventId, final String jsonBody) {
        final Guest guest = GSON.fromJson(jsonBody, Guest.class);
        guest.setEventId(eventId);
        datastore.save(guest);
    }
}
