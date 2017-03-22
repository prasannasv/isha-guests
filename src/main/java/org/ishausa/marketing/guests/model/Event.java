package org.ishausa.marketing.guests.model;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;
import java.util.Set;

/**
 * A public event organized by Isha Foundation.
 *
 * Created by Prasanna Venkat on 12/30/2016.
 */
@Data
@Entity("events")
public class Event {
    @Id
    private String id;

    private Set<EventType> offerings;
    private Date eventDate;
    private Center nearestCenter;

    /** userId of the User that created this event. */
    private String creator;
    private Date createdAt;
}
