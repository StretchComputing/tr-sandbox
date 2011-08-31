package com.stretchcom.sandbox.server;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class SandboxApplication extends Application {

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a new instance of
        // SandboxRestlet.
        Router router = new Router(getContext());

        // Defines only one route.
        router.attach("/sandbox", SandboxResource.class);
        router.attach("/users", UsersResource.class);
        router.attach("/users/{key}", UsersResource.class);

        return router;
    }
}
