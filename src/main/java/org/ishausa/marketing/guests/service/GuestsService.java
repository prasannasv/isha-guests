package org.ishausa.marketing.guests.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.silverpop.api.client.ApiClient;
import com.silverpop.api.client.ApiResultException;
import com.silverpop.api.client.command.AddRecipientCommand;
import com.silverpop.api.client.command.elements.ColumnElementType;
import com.silverpop.api.client.xmlapi.XmlApiClient;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.ishausa.marketing.guests.model.Event;
import org.ishausa.marketing.guests.model.Guest;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.FindAndModifyOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public GuestsService(final Datastore datastore) {
        this.datastore = datastore;
    }

    public List<Guest> findForEventId(final String eventId) {
        return datastore.createQuery(Guest.class)
                .filter("eventId =", eventId)
                .asList();
    }

    public void addGuest(final Event event, final String jsonBody) {
        final Guest guest = GSON.fromJson(jsonBody, Guest.class);
        guest.setEventId(event.getId());
        addGuest(getSilverPopClient(), event, guest);
    }

    private ApiClient getSilverPopClient() {
        return new XmlApiClient(
                getEnvPropOrThrow("SILVERPOP_API_URL"),
                getEnvPropOrThrow("SILVERPOP_API_USERNAME"),
                getEnvPropOrThrow("SILVERPOP_API_PASSWORD"));
    }

    private void addGuest(final ApiClient apiClient, final Event event, final Guest guest) {
        log.info("adding guest: " + guest);

        try {
            final AddRecipientCommand addRecipientCommand = new AddRecipientCommand();

            addRecipientCommand.setListId(Integer.parseInt(getEnvPropOrThrow("SILVERPOP_DATABASE_ID")));
            addRecipientCommand.setCreatedFrom(SILVERPOP_CREATED_FROM_OPTED_IN);
            addRecipientCommand.setUpdateIfFound(true);

            addRecipientCommand.addColumn(createColumn("First Name", guest.getFirstName()));
            addRecipientCommand.addColumn(createColumn("Last Name", guest.getLastName()));
            addRecipientCommand.addColumn(createColumn("Email", guest.getEmail()));
            addRecipientCommand.addColumn(createColumn("gc_event_id", event.createEventId()));

            apiClient.executeCommand(addRecipientCommand);

            insertOrUpdate(guest);
        } catch (final ApiResultException e) {
            log.log(Level.WARNING, "Failed to add recipient: " + guest + " in silverpop", e);
        }
    }

    private void insertOrUpdate(final Guest guest) {
        final Query<Guest> lookupByEmail = datastore.createQuery(Guest.class)
                .filter("email =", guest.getEmail());

        final UpdateOperations<Guest> updateOperations = datastore.createUpdateOperations(Guest.class)
                .set("firstName", guest.getFirstName())
                .set("lastName", guest.getLastName())
                .set("eventId", guest.getEventId());

        final FindAndModifyOptions options = new FindAndModifyOptions()
                .upsert(true);

        datastore.findAndModify(lookupByEmail, updateOperations, options);
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

    public void addGuests(final Event event, final InputStream in) {
        final ApiClient silverPopClient = getSilverPopClient();
        try {
            final CSVParser parser =
                    CSVFormat.EXCEL
                            .withHeader()
                            .withIgnoreHeaderCase()
                            .withSkipHeaderRecord()
                            .withTrim()
                            .withNullString("N/A")
                            .parse(new InputStreamReader(in));

            for (final CSVRecord record : parser.getRecords()) {
                log.info("record: " + record);

                final String firstName = record.get("First Name");
                final String lastName = record.get("Last Name");
                final String email = record.get("Email");

                final Guest guest = Guest.createForEvent(event, firstName, lastName, email);

                addGuest(silverPopClient, event, guest);
            }
        } catch (final IOException e) {
            log.log(Level.WARNING, "Failed adding multiple guests", e);
        }
    }
}
