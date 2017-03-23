package org.ishausa.marketing.guests.service;

import org.ishausa.marketing.guests.model.Event;
import org.ishausa.marketing.guests.model.Role;
import org.ishausa.marketing.guests.model.User;

import java.util.List;

/**
 * Created by Prasanna Venkat on 1/7/2017.
 */
public class EventListResponse {
    private Role userRole;
    private List<Event> events;

    public EventListResponse(final User user, final List<Event> events) {
        this.userRole = user.getRole();
        this.events = events;
    }
}
