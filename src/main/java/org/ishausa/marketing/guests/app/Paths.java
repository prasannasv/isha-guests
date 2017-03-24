package org.ishausa.marketing.guests.app;

/**
 * Created by Prasanna Venkat on 12/30/2016.
 */
public class Paths {
    public static final String STATIC = "/static";
    public static final String MAIN = "/";
    public static final String LOGIN = "/login";
    public static final String VALIDATE_ID_TOKEN = "/validate_id_token";

    private static final String API_VERSION = "/api/v1";

    public static final String EVENTS = API_VERSION + "/events";
    public static final String ID_PARAM = ":id";
    public static final String EVENT_BY_ID = EVENTS + "/" + ID_PARAM;

    public static final String GUEST_FOR_EVENT_ID = EVENT_BY_ID + "/guest";
    public static final String GUESTS_FOR_EVENT_ID = EVENT_BY_ID + "/guests";

    public static final String CENTERS = API_VERSION + "/centers";
    public static final String USERS = API_VERSION + "/users";
    public static final String PROMOTE_USER = USERS + "/" + ID_PARAM + "/promote";
    public static final String DEMOTE_USER = USERS + "/" + ID_PARAM + "/demote";
}
