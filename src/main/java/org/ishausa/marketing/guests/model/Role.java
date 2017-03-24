package org.ishausa.marketing.guests.model;

/**
 * A hierarchy of roles in this system.
 *
 * Created by Prasanna Venkat on 1/2/2017.
 */
public enum Role {
    // ordered from least privileged to most privileged.
    REGULAR,
    EVENT_ADMIN,
    ADMIN,
}
