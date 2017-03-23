package org.ishausa.marketing.guests.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.silverpop.api.client.ApiClient;
import com.silverpop.api.client.ApiResultException;
import com.silverpop.api.client.command.AddRecipientCommand;
import com.silverpop.api.client.command.elements.ColumnElementType;
import com.silverpop.api.client.xmlapi.XmlApiClient;
import org.ishausa.marketing.guests.model.Event;
import org.ishausa.marketing.guests.model.Guest;
import org.mongodb.morphia.Datastore;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Prasanna Venkat on 3/22/2017.
 */
public class GuestsService {
    private static final Logger log = Logger.getLogger(GuestsService.class.getName());

    private static final Gson GSON = new GsonBuilder().create();

    // Google silverpop xml api developer guide to download a pdf that explains the meanings of this field.
    private static final int SILVERPOP_CREATED_FROM_OPTED_IN = 2;

    private final Datastore datastore;

    private final ApiClient silverPopClient;

    public GuestsService(final Datastore datastore) {
        this.datastore = datastore;
        this.silverPopClient = new XmlApiClient(
                getEnvPropOrThrow("SILVERPOP_API_URL"),
                getEnvPropOrThrow("SILVERPOP_API_USERNAME"),
                getEnvPropOrThrow("SILVERPOP_API_PASSWORD"));
    }

    public List<Guest> findForEventId(final String eventId) {
        return datastore.createQuery(Guest.class)
                .filter("eventId =", eventId)
                .asList();
    }

    public void addGuest(final Event event, final String jsonBody) {
        final Guest guest = GSON.fromJson(jsonBody, Guest.class);
        guest.setEventId(event.getId());

        try {
            final AddRecipientCommand addRecipientCommand = new AddRecipientCommand();

            addRecipientCommand.setListId(Integer.parseInt(getEnvPropOrThrow("SILVERPOP_DATABASE_ID")));
            addRecipientCommand.setCreatedFrom(SILVERPOP_CREATED_FROM_OPTED_IN);
            addRecipientCommand.setUpdateIfFound(true);

            addRecipientCommand.addColumn(createColumn("First Name", guest.getFirstName()));
            addRecipientCommand.addColumn(createColumn("Last Name", guest.getLastName()));
            addRecipientCommand.addColumn(createColumn("Email", guest.getEmail()));
            addRecipientCommand.addColumn(createColumn("gc_event_id", event.createEventId()));

            silverPopClient.executeCommand(addRecipientCommand);

            datastore.save(guest);
        } catch (final ApiResultException e) {
            log.log(Level.WARNING, "Failed to add recipient: " + guest + " in silverpop", e);
        }
    }

    private String getEnvPropOrThrow(final String propertyName) {
        final String value = System.getenv(propertyName);
        if (value == null) {
            throw new IllegalStateException("Property: " + propertyName + " not set");
        }
        return value;
    }

    private ColumnElementType createColumn(final String name, final String value) {
        final ColumnElementType column = new ColumnElementType();

        column.setName(name);
        column.setValue(value);

        return column;
    }
}
