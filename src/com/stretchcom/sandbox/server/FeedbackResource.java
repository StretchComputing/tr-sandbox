package com.stretchcom.sandbox.server;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

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

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.stretchcom.sandbox.server.EMF;

public class FeedbackResource extends ServerResource {
	private static final Logger log = Logger.getLogger(FeedbackResource.class.getName());
    private String feedbackId;

    @Override
    protected void doInit() throws ResourceException {
        log.info("in doInit");
        feedbackId = (String) getRequest().getAttributes().get("id");
    }

    @Get("json")
    public JsonRepresentation get(Variant variant) {
         JSONObject jsonReturn;

        log.info("in get");
        if (feedbackId != null) {
            // Get Feedback Info API
        	log.info("in Get Feedback Info API");
        	jsonReturn = getFeedbackInfoJson(feedbackId);
        } else {
            // Get List of Feedbacks API
        	log.info("Get List of Feedbacks API");
        	jsonReturn = getListOfFeedbacksJson();
        }
        
        return new JsonRepresentation(jsonReturn);
    }

    // Handles 'Create a new feedback' API
    @Post("json")
    public JsonRepresentation createFeedback(Representation entity) {
    	log.info("createFeedback(@Post) entered ..... ");
        JSONObject jsonReturn = new JSONObject();
		EntityManager em = EMF.get().createEntityManager();
		
		String apiStatus = ApiStatusCode.SUCCESS;
		Feedback feedback = new Feedback();
		this.setStatus(Status.SUCCESS_CREATED);
		em.getTransaction().begin();
        try {
			JsonRepresentation jsonRep = new JsonRepresentation(entity);
			log.info("jsonRep = " + jsonRep.toString());
			JSONObject json = jsonRep.getJsonObject();
			
			if(json.has("voice")) {
				feedback.setVoiceBase64(json.getString("voice"));
				//feedback.setVoiceBase64("this is not voice data");
				log.info("stored voice value = " + feedback.getVoiceBase64());
			} else {
				log.info("no JSON voice field found");
			}
			
			if(json.has("userName")) {
				feedback.setUserName(json.getString("userName"));
			}
			
			if(json.has("recordedDate")) {
				String recordedDateStr = json.getString("recordedDate");
				Date gmtRecordedDate = GMT.stringToDate(recordedDateStr, null);
				if(gmtRecordedDate == null) {
					log.info("invalid reocrded date format passed in");
				}
				feedback.setRecordedDate(gmtRecordedDate);
			}
			
			if(json.has("instanceUrl")) {
				feedback.setInstanceUrl(json.getString("instanceUrl"));
			}
			
			// Default status to 'new'
			feedback.setStatus(Feedback.NEW_STATUS);
		    
			em.persist(feedback);
			em.getTransaction().commit();
			
			String keyWebStr = KeyFactory.keyToString(feedback.getKey());
			log.info("feedback with key " + keyWebStr + " created successfully");

			// TODO URL should be filtered to have only legal characters
			String baseUri = this.getRequest().getHostRef().getIdentifier();
			this.getResponse().setLocationRef(baseUri + "/");

			jsonReturn.put("feedbackId", keyWebStr);
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
    
    private JSONObject getFeedbackInfoJson(String theFeedbackId) {
       	EntityManager em = EMF.get().createEntityManager();
    	JSONObject jsonReturn = new JSONObject();
    	
		String apiStatus = ApiStatusCode.SUCCESS;
		this.setStatus(Status.SUCCESS_OK);
		try {
			Key feedbackKey = KeyFactory.stringToKey(this.feedbackId);
    		Feedback feedback = null;
    		feedback = (Feedback)em.createNamedQuery("Feedback.getByKey")
				.setParameter("key", feedbackKey)
				.getSingleResult();

    		jsonReturn.put("feedbackId", KeyFactory.keyToString(feedback.getKey()));
			
        	Date recordedDate = feedback.getRecordedDate();
        	// TODO support time zones
        	if(recordedDate != null) jsonReturn.put("recordedDate", GMT.convertToLocalDate(recordedDate, null));
        	
        	jsonReturn.put("userName", feedback.getUserName());
        	jsonReturn.put("instanceUrl", feedback.getInstanceUrl());
        	
        	// TODO remove eventually, for backward compatibility before status field existed. If status not set, default to 'new'
        	String status = feedback.getStatus();
        	if(status == null || status.length() == 0) {status = "new";}
        	jsonReturn.put("status", status);
        	
        	jsonReturn.put("voice", feedback.getVoiceBase64());
            	
		} catch (JSONException e) {
			log.severe("error converting json representation into a JSON object");
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
		} catch (NoResultException e) {
			// feedback ID passed in is not valid
			apiStatus = ApiStatusCode.FEEDBACK_NOT_FOUND;
		} catch (NonUniqueResultException e) {
			log.severe("should never happen - two or more games have same key");
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
		} 
    	
		try {
			jsonReturn.put("apiStatus", apiStatus);
		} catch (JSONException e) {
			log.severe("error converting json representation into a JSON object");
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
		}

		return jsonReturn;
    }
    
    private JSONObject getListOfFeedbacksJson() {
       	EntityManager em = EMF.get().createEntityManager();
    	JSONObject jsonReturn = new JSONObject();
    	
		String apiStatus = ApiStatusCode.SUCCESS;
		this.setStatus(Status.SUCCESS_OK);
		try {
			// cannot use a NamedQuery for a batch get of keys
			List<Feedback> feedbacks = (List<Feedback>)em.createNamedQuery("Feedback.getAll").getResultList();

			JSONArray feedbackJsonArray = new JSONArray();
			for (Feedback fb : feedbacks) {
				JSONObject feedbackJsonObj = new JSONObject();
				
				feedbackJsonObj.put("feedbackId", KeyFactory.keyToString(fb.getKey()));
				
            	Date recordedDate = fb.getRecordedDate();
            	// TODO support time zones
            	if(recordedDate != null) feedbackJsonObj.put("recordedDate", GMT.convertToLocalDate(recordedDate, null));
            	
            	feedbackJsonObj.put("userName", fb.getUserName());
            	feedbackJsonObj.put("instanceUrl", fb.getInstanceUrl());
            	
            	// TODO remove eventually, for backward compatibility before status field existed. If status not set, default to 'new'
            	String status = fb.getStatus();
            	if(status == null || status.length() == 0) {status = "new";}
            	feedbackJsonObj.put("status", status);
				
				feedbackJsonArray.put(feedbackJsonObj);
			}
			jsonReturn.put("feedback", feedbackJsonArray);
		} catch (JSONException e) {
			log.severe("error converting json representation into a JSON object");
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
		} catch (Exception e) {
			log.severe("getListOfFeedbacksJson(): exception = " + e.getMessage());
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
		}
    	
		try {
			jsonReturn.put("apiStatus", apiStatus);
		} catch (JSONException e) {
			log.severe("error converting json representation into a JSON object");
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
		}

		return jsonReturn;
    }
    
}
