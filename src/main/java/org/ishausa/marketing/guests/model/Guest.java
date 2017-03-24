package org.ishausa.marketing.guests.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by Prasanna Venkat on 3/22/2017.
 */
@Data
@Entity("guests")
public class Guest {
    @Id
    private ObjectId id;

    private String eventId;

    private String firstName;
    private String lastName;
    private String email;

    public static Guest createForEvent(final Event event,
                                       final String firstName,
                                       final String lastName,
                                       final String email) {
        final Guest guest = new Guest();

        guest.setEventId(event.getId());
        guest.setFirstName(firstName);
        guest.setLastName(lastName);
        guest.setEmail(email);

        return guest;
    }
}
