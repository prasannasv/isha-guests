package org.ishausa.marketing.guests.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * An Isha Center / Branch.
 *
 * Created by Prasanna Venkat on 3/21/2017.
 */
@Data
@Entity("centers")
public class Center {
    @Id
    private ObjectId id;
    private String cityName;
    private String stateCode;
    private String country;
    private String centerCode;
}
