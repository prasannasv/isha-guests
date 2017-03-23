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
    private String countryCode;
    private String centerCode;

    public static Center seattle() {
        return createCenter("Seattle", "US", "STLC");
    }

    public static Center losAngeles() {
        return createCenter("Los Angeles", "US", "LASC");
    }

    public static Center createCenter(final String cityName, final String countryCode, final String centerCode) {
        final Center center = new Center();

        center.setCityName(cityName);
        center.setCountryCode(countryCode);
        center.setCenterCode(centerCode);

        return center;
    }
}
