package com.stretchcom.sandbox.server;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class SandboxResource extends ServerResource {

    @Get
    public String represent() {
        return "hello, world (from the cloud!)";
    }

    @Put
    public String updateUser(Representation entity) {
       return "this was a put";
    }
}
