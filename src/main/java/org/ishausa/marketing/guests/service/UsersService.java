package org.ishausa.marketing.guests.service;

import org.ishausa.marketing.guests.model.Role;
import org.ishausa.marketing.guests.model.User;
import org.mongodb.morphia.Datastore;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Prasanna Venkat on 1/2/2017.
 */
public class UsersService {
    private static final Logger log = Logger.getLogger(UsersService.class.getName());

    private final Datastore datastore;

    public UsersService(final Datastore datastore) {
        this.datastore = datastore;
    }

    public User findById(final String userId) {
        return datastore.get(User.class, userId);
    }

    public void createOrUpdate(final User user) {
        final User existingUser = findById(user.getUserId());
        // Return if no fields have changed except the role.
        if (existingUser != null) {
            // this will let us change the role directly in the database without it getting overwritten
            user.setRole(existingUser.getRole());
        }
        // We do an update of the existing user document because the user may have changed their
        // first name or even email id.
        if (user.equals(existingUser)) {
            return;
        }

        if (existingUser == null) {
            log.info("Creating a new user: " + user);
        } else {
            log.info("User info has changed. Old: " + existingUser + ", new: " + user);
        }

        datastore.save(user);
    }

    public List<User> listAll() {
        return datastore.find(User.class)
                .asList();
    }

    public void promote(final String userId) {
        final User user = findById(userId);
        switch (user.getRole()) {
            case REGULAR:
                user.setRole(Role.EVENT_ADMIN);
                break;
            case EVENT_ADMIN:
                user.setRole(Role.ADMIN);
                break;
            default:
                return;
        }

        datastore.save(user);
    }

    public void demote(final String userId) {
        final User user = findById(userId);
        switch (user.getRole()) {
            case ADMIN:
                user.setRole(Role.EVENT_ADMIN);
                break;
            case EVENT_ADMIN:
                user.setRole(Role.REGULAR);
                break;
            default:
                return;
        }

        datastore.save(user);
    }
}
