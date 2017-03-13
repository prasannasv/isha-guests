package org.ishausa.marketing.guests.model;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by Prasanna Venkat on 12/30/2016.
 */
@Data
@Entity("users")
public class User {
    @Id
    private String userId;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;

    User() {
        //for deserializer
    }

    public User(final String userId,
                final String name,
                final String firstName,
                final String lastName,
                final String email,
                final Role role) {
        this.userId = userId;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }
}
