package org.ishausa.marketing.guests.model;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
    @Reference
    private Center nearestCenter;

    /** userId of the User that created this event. */
    private String creator;
    private Date createdAt;

    public String createEventId() {
        final StringBuilder builder = new StringBuilder();

        builder.append(nearestCenter.getCenterCode()).append('_');
        final List<EventType> sortedOfferings = new ArrayList<>(offerings);
        Collections.sort(sortedOfferings);
        for (final EventType offering : sortedOfferings) {
            builder.append(offering).append('#');
        }
        final LocalDateTime ldt = LocalDateTime.ofInstant(eventDate.toInstant(), ZoneId.of("UTC"));
        builder.append(ldt.format(DateTimeFormatter.ISO_LOCAL_DATE));

        return builder.toString();
    }
}
