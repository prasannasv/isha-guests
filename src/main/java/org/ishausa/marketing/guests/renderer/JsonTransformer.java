package org.ishausa.marketing.guests.renderer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.ResponseTransformer;

/**
 * Created by Prasanna Venkat on 12/31/2016.
 */
public class JsonTransformer implements ResponseTransformer {

    private static final Gson GSON = new GsonBuilder().create();

    @Override
    public String render(final Object model) throws Exception {
        return GSON.toJson(model);
    }
}
