package org.ishausa.marketing.guests.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bson.types.ObjectId;
import org.ishausa.marketing.guests.model.Event;
import org.ishausa.marketing.guests.model.Role;
import org.ishausa.marketing.guests.model.User;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Prasanna Venkat on 12/30/2016.
 */
public class EventsService {
    private static final Logger log = Logger.getLogger(EventsService.class.getName());

    private static final Gson GSON = new GsonBuilder().create();

    private final Datastore datastore;

    public EventsService(final Datastore datastore) {
        this.datastore = datastore;
    }

    public List<Event> listAll() {
        return datastore.find(Event.class).asList();
    }

    public String createEvent(final User creator, final String jsonBody) {
        if (Role.REGULAR.equals(creator.getRole())) {
            throw new IllegalStateException("Regular users are not allowed to create events");
        }
        final Event event = GSON.fromJson(jsonBody, Event.class);
        event.setCreator(creator.getUserId());
        event.setCreatedAt(new Date());

        final Key<Event> key = datastore.save(event);

        log.info("Created event: " + event + ", from input json: " + jsonBody);

        return (String) key.getId();
    }

    public Event find(final String id) {
        final Event event = datastore.get(Event.class, new ObjectId(id));

        log.info("event for id: " + id + ", event: " + event);

        return event;
    }
}
