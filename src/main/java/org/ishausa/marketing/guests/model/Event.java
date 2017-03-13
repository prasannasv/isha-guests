package org.ishausa.marketing.guests.model;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;

/**
 * Created by Prasanna Venkat on 12/30/2016.
 */
@Data
@Entity("trips")
public class Event {
    @Id
    private String id;
    private String from;
    private String to;
    private Date departureDateAndTime;
    /** userId of the User that created this trip. */
    private String creator;
    private Date createdAt;
}
