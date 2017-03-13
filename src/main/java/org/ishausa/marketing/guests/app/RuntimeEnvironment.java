package org.ishausa.marketing.guests.app;

import spark.utils.StringUtils;

/**
 * Created by Prasanna Venkat on 1/1/2017.
 */
public class RuntimeEnvironment {
    public static boolean isOnHeroku() {
        return StringUtils.isNotBlank(System.getenv("ON_HEROKU"));
    }
}
