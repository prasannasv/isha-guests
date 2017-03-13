package org.ishausa.marketing.guests.store;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Created by Prasanna Venkat on 12/31/2016.
 */
public class MongoDb {
    private static volatile MongoClient mongoClient;

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = new MongoClient("localhost");
        }

        return mongoClient.getDatabase("carpool-app");
    }
}
