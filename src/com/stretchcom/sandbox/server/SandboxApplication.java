package com.stretchcom.sandbox.server;

import java.util.logging.Logger;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class SandboxApplication extends Application {
    private static final Logger log = Logger.getLogger(SandboxApplication.class.getName());
    public static final String APPLICATION_BASE_URL = "https://mobile.pulse.tr-sandbox.appspot.com/";
    public static final String LIST_DATE_FORMAT = "MM/dd/yy kk:mm";
    public static final String INFO_DATE_FORMAT = "MM/dd/yyyy 'at' hh:mm a";
    public static final String DEFAULT_LOCAL_TIME_ZONE = "America/Chicago";

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createInboundRoot() {
        Router router = new Router(getContext());
        log.info("in createInboundRoot");

        router.attach("/sandbox", SandboxResource.class);
        router.attach("/users", UsersResource.class);
        router.attach("/users/{id}", UsersResource.class);
        
        router.attach("/crashDetects", CrashDetectResource.class);
        router.attach("/crashDetects/{id}", CrashDetectResource.class);
        
        // TODO remove "feedbacks" mappings. There for backward compatibility during Beta testing.
        router.attach("/feedbacks", FeedbackResource.class);
        router.attach("/feedbacks/{id}", FeedbackResource.class);
        router.attach("/feedback", FeedbackResource.class);
        router.attach("/feedback/{id}", FeedbackResource.class);
        
        router.attach("/clientLogs", ClientLogResource.class);
        router.attach("/clientLogs/{id}", ClientLogResource.class);

        return router;
    }
}
