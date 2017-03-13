package org.ishausa.marketing.guests.model;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;

/**
 * Created by Prasanna Venkat on 12/30/2016.
 */
@Data
@Entity("requests")
public class RideRequest {
    @Id
    private String id;
    private String tripId;
    private String userId;
    private int seatsRequested;
    private Date requestedOn;

    RideRequest() {
        // for deserializer
    }

    public RideRequest(final String tripId,
                       final String userId,
                       final int seatsRequested,
                       final Date requestedOn) {
        this.tripId = tripId;
        this.userId = userId;
        this.seatsRequested = seatsRequested;
        this.requestedOn = requestedOn;
    }
}
