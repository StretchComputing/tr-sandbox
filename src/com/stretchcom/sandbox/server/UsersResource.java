package com.stretchcom.sandbox.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.KeyFactory;
import com.stretchcom.sandbox.models.User;

public class UsersResource extends ServerResource {
    private static final Logger log = Logger.getLogger(UsersResource.class.getName());
    private String id;

    @Override
    protected void doInit() throws ResourceException {
        id = (String) getRequest().getAttributes().get("id");
    }

    @Get("json")
    public JsonRepresentation get(Variant variant) {
        log.info("in getUsers");
        if (id != null) {
            return show(id);
        } else {
            return index();
        }
    }

    @Post("json")
    public JsonRepresentation post(Representation entity) {
        log.info("in createUser");
        JSONObject out = new JSONObject();
        EntityManager em = EMF.get().createEntityManager();
        this.setStatus(Status.SUCCESS_CREATED);

        User user = null;
        em.getTransaction().begin();
        try {
            user = new User();
            JSONObject in = new JsonRepresentation(entity).getJsonObject();
            if (in.has("first_name")) {
                user.setFirstName(in.getString("first_name"));
            }
            if (in.has("last_name")) {
                user.setLastName(in.getString("last_name"));
            }
            if (in.has("email_address")) {
                user.setEmailAddress(in.getString("email_address"));
            }
            em.persist(user);
            em.getTransaction().commit();
            out.put("first_name", user.getFirstName());
            out.put("last_name", user.getLastName());
            out.put("email_address", user.getEmailAddress());
            out.put("key", KeyFactory.keyToString(user.getKey()));
        } catch (IOException e) {
            log.severe("error extracting JSON object from Post");
            e.printStackTrace();
            this.setStatus(Status.SERVER_ERROR_INTERNAL);
        } catch (JSONException e) {
            e.printStackTrace();
            this.setStatus(Status.SERVER_ERROR_INTERNAL);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
        return new JsonRepresentation(out);
    }

    private JsonRepresentation index() {
        log.info("in getAllUsers");
        JSONObject out = new JSONObject();
        EntityManager em = EMF.get().createEntityManager();
        try {
            List<User> users = new ArrayList<User>();
            JSONArray ja = new JSONArray();
            users = (List<User>) em.createNamedQuery("User.getAll").getResultList();
            for (User u : users) {
                JSONObject jo = new JSONObject();
                jo.put("user_id", KeyFactory.keyToString(u.getKey()));
                jo.put("first_name", u.getFirstName());
                jo.put("last_name", u.getLastName());
                jo.put("email_address", u.getEmailAddress());
                ja.put(jo);
            }
            out.put("users", ja);
        } catch (JSONException e) {
            e.printStackTrace();
            this.setStatus(Status.SERVER_ERROR_INTERNAL);
        }
        return new JsonRepresentation(out);
    }

    private JsonRepresentation show(String id) {
        log.info("in getUser");
        JSONObject out = new JSONObject();
        try {
            out.put("message", "from the show method");
        } catch (JSONException e) {
            e.printStackTrace();
            this.setStatus(Status.SERVER_ERROR_INTERNAL);
        }
        return new JsonRepresentation(out);
    }
}
