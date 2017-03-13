package org.ishausa.marketing.guests.model;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;

/**
 * Created by Prasanna Venkat on 12/30/2016.
 */
@Data
@Entity("offers")
public class RideOffer {
    @Id
    private String id;
    private String tripId;
    private String userId;
    private int seatsOffered;
    private MatchType matchType;
    private Date offeredOn;

    RideOffer() {
        // for deserializer
    }

    public RideOffer(final String tripId,
                     final String userId,
                     final int seatsOffered,
                     final MatchType matchType,
                     final Date offeredOn) {
        this.tripId = tripId;
        this.userId = userId;
        this.seatsOffered = seatsOffered;
        this.matchType = matchType;
        this.offeredOn = offeredOn;
    }
}
