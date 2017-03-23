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
}
