package org.ishausa.marketing.guests.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ishausa.marketing.guests.app.Paths;
import org.ishausa.marketing.guests.model.Role;
import org.ishausa.marketing.guests.model.User;
import org.ishausa.marketing.guests.service.UsersService;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.logging.Logger;

import static spark.Spark.halt;

/**
 * Authenticates an user using Sign-in with Google.
 *
 * Steps: https://developers.google.com/identity/sign-in/web/devconsole-project
 *
 * Created by Prasanna Venkat on 12/30/2016.
 */
public class AuthenticationHandler {
    private static final Logger log = Logger.getLogger(AuthenticationHandler.class.getName());

    private static final Gson GSON = new GsonBuilder().create();

    /* OAuth 2.0 related stuff */
    // This constant is also defined in .login soy template.
    private static final String CLIENT_ID = "837673118886-7s83918flvprea0e4neurojg01n4ue55.apps.googleusercontent.com";
    private static volatile HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /* session attributes */
    private static final String SESSION_ID = "session_id";
    public static final String NAME = "name";

    /* request params */
    private static final String ID_TOKEN = "id_token";

    /* request attributes */
    public static final String AUTHENTICATED_USER = "authenticated_user";

    private final UsersService usersService;

    public AuthenticationHandler(final UsersService usersService) {
        this.usersService = usersService;
    }

    public void ensureAuthenticated(final Request request, final Response response) throws Exception {
        final String sessionId = request.session().attribute(SESSION_ID);
        final User user = getUser(sessionId);

        final String path = request.pathInfo();
        if (!Paths.LOGIN.equals(path) &&
                !Paths.VALIDATE_ID_TOKEN.equals(path) &&
                user == null) {

            log.info("redirecting unauthenticated request to: " + path + " to: " + Paths.LOGIN);
            response.redirect(Paths.LOGIN);

            halt(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            request.attribute(AUTHENTICATED_USER, user);
        }
    }

    private User getUser(final String sessionId) {
        return StringUtils.isNotBlank(sessionId) ? usersService.findById(sessionId) : null;
    }

    public String finishLogin(final Request request, final Response response)
            throws GeneralSecurityException, IOException {

        response.type("application/json");

        final String idTokenString = request.queryParams(ID_TOKEN);

        // Validate the idTokenString
        // Based on https://developers.google.com/identity/sign-in/web/backend-auth
        if (httpTransport == null) {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        }
        log.info("Creating a verifier for audience ClientID: " + CLIENT_ID);
        final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, JSON_FACTORY)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        final GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            final GoogleIdToken.Payload payload = idToken.getPayload();

            final String userId = payload.getSubject();
            request.session().attribute(SESSION_ID, userId);
            request.session().attribute(NAME, payload.get("name"));

            createOrUpdateUser(payload);

            // Learning:
            // POST can't redirect to another path (http://stackoverflow.com/questions/34992607/spark-java-redirect-browser-does-not-redirect)
            return GSON.toJson(LoginResponse.success());
        } else {
            return GSON.toJson(LoginResponse.failure("Invalid Id token"));
        }
    }

    private void createOrUpdateUser(final GoogleIdToken.Payload payload) {
        final User user = new User(payload.getSubject(),
                (String) payload.get("name"),
                (String) payload.get("given_name"),
                (String) payload.get("family_name"),
                payload.getEmail(),
                Role.REGULAR);

        usersService.createOrUpdate(user);
    }

    private static class LoginResponse {
        private boolean isSuccess;
        private String redirectPath;
        private String errorMessage;

        LoginResponse(final boolean isSuccess, final String redirectPath, final String errorMessage) {
            this.isSuccess = isSuccess;
            this.redirectPath = redirectPath;
            this.errorMessage = errorMessage;
        }

        static LoginResponse success() {
            return new LoginResponse(true, Paths.MAIN, "");
        }

        static LoginResponse failure(final String errorMessage) {
            return new LoginResponse(false, "", errorMessage);
        }
    }
}
