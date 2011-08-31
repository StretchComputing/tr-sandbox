package com.stretchcom.sandbox.server;

import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class UsersResource extends ServerResource {
    private String id;

    @Override
    protected void doInit() throws ResourceException {
        id = (String) getRequest().getAttributes().get("id");
    }

    @Get("html")
    public String getHtml(Variant variant) {
        if (id != null) {
            return get(id);
        } else {
            return getAll();
        }
    }

    private String getAll() {
        return "getting all users";
    }

    private String get(String id) {
        return "getting a specific user";
    }
}
