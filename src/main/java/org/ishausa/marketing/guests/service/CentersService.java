package org.ishausa.marketing.guests.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ishausa.marketing.guests.model.Center;
import org.ishausa.marketing.guests.model.Role;
import org.ishausa.marketing.guests.model.User;
import org.mongodb.morphia.Datastore;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Prasanna Venkat on 3/21/2017.
 */
public class CentersService {
    private static final Logger log = Logger.getLogger(CentersService.class.getName());
    private static final Gson GSON = new GsonBuilder().create();

    private final Datastore datastore;

    public CentersService(final Datastore datastore) {
        this.datastore = datastore;
    }

    public void createCenter(final User creator, final String jsonBody) {
        if (Role.REGULAR.equals(creator.getRole())) {
            throw new IllegalStateException("Regular users are not allowed to create centers");
        }
        log.info("Creating center from json: " + jsonBody);
        final Center center = GSON.fromJson(jsonBody, Center.class);

        datastore.save(center);
    }

    public List<Center> listAll() {
        return datastore.find(Center.class).asList();
    }
}
