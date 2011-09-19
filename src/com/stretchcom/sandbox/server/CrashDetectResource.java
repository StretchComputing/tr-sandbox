package com.stretchcom.sandbox.server;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.KeyFactory;

public class CrashDetectResource extends ServerResource {
	private static final Logger log = Logger.getLogger(CrashDetectResource.class.getName());

    @Get
    public String represent() {
        return "hello, world (from the cloud!)";
    }

    // Handles 'Create a new crash detect' API
    @Post("json")
    public JsonRepresentation createCrashDetect(Representation entity) {
    	log.info("createCrashDetect(@Post) entered ..... ");
        JSONObject jsonReturn = new JSONObject();
		EntityManager em = EMF.get().createEntityManager();
		
		String apiStatus = ApiStatusCode.SUCCESS;
		CrashDetect crashDetect = new CrashDetect();
		this.setStatus(Status.SUCCESS_CREATED);
		em.getTransaction().begin();
        try {
			JsonRepresentation jsonRep = new JsonRepresentation(entity);
			log.info("jsonRep = " + jsonRep.toString());
			JSONObject json = jsonRep.getJsonObject();
			
			if(json.has("summary")) {
				crashDetect.setSummary(json.getString("summary"));
			}
			
			if(json.has("userName")) {
				crashDetect.setUserName(json.getString("userName"));
			}
			
			if(json.has("stackData")) {
				crashDetect.setStackDataBase64(json.getString("stackData"));
			}
			
			if(json.has("detectedDate")) {
				String detectedDateStr = json.getString("detectedDate");
				Date gmtDetectedDate = GMT.stringToDate(detectedDateStr, null);
				if(gmtDetectedDate == null) {
					log.info("invalid detected date format passed in");
				}
				crashDetect.setDetectedDate(gmtDetectedDate);
			}
			em.persist(crashDetect);
			em.getTransaction().commit();
			
			String keyWebStr = KeyFactory.keyToString(crashDetect.getKey());
			log.info("crash detect with key " + keyWebStr + " created successfully");

			// TODO URL should be filtered to have only legal characters
			String baseUri = this.getRequest().getHostRef().getIdentifier();
			this.getResponse().setLocationRef(baseUri + "/");

			jsonReturn.put("crashDetectId", keyWebStr);
		} catch (IOException e) {
			log.severe("error extracting JSON object from Post");
			e.printStackTrace();
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
		} catch (JSONException e) {
			log.severe("error converting json representation into a JSON object");
			e.printStackTrace();
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
		} finally {
		    if (em.getTransaction().isActive()) {
		        em.getTransaction().rollback();
		    }
		    em.close();
		}
																																																																					
		try {
			jsonReturn.put("apiStatus", apiStatus);
		} catch (JSONException e) {
			log.severe("error creating JSON return object");
			e.printStackTrace();
		}
		return new JsonRepresentation(jsonReturn);
    }
}
