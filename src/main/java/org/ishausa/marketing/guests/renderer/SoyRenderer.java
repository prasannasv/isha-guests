package org.ishausa.marketing.guests.renderer;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Prasanna Venkat on 12/29/2016.
 */
public class SoyRenderer {
    public enum EventGuestsAppTemplate {
        LOGIN,
        MAIN;

        @Override
        public String toString() {
            return "." + name().toLowerCase();
        }
    }

    public static final SoyRenderer INSTANCE = new SoyRenderer();

    private final SoyTofu serviceTofu;

    private SoyRenderer() {
        final SoyFileSet sfs = SoyFileSet.builder()
                .add(new File("./src/main/webapp/template/event_guests_app.soy"))
                .build();
        serviceTofu = sfs.compileToTofu().forNamespace("org.ishausa.marketing.guests.app");
    }

    public String render(final EventGuestsAppTemplate template) {
        return render(template, new HashMap<>());
    }

    public String render(final EventGuestsAppTemplate template, final Map<String, ?> data) {
        return serviceTofu.newRenderer(template.toString()).setData(data).render();
    }

}
