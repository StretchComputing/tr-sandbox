package com.stretchcom.sandbox.server;

import java.util.logging.Logger;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class SandboxApplication extends Application {
    private static final Logger log = Logger.getLogger(SandboxApplication.class.getName());

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createInboundRoot() {
        Router router = new Router(getContext());

        router.attach("/sandbox", SandboxResource.class);
        router.attach("/users", UsersResource.class);
        router.attach("/users/{id}", UsersResource.class);

        return router;
    }
}
