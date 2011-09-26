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
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class CrashDetectResource extends ServerResource {
	private static final Logger log = Logger.getLogger(CrashDetectResource.class.getName());
	private String crashDetectId;

    @Override
    protected void doInit() throws ResourceException {
        log.info("in doInit");
        crashDetectId = (String) getRequest().getAttributes().get("id");
    }

    @Get("json")
    public JsonRepresentation get(Variant variant) {
         JSONObject jsonReturn;

        log.info("in get for Crash Detect resource");
        if (this.crashDetectId != null) {
            // Get Crash Detect Info API
        	log.info("in Get Feedback Info API");
        	jsonReturn = getCrashDetectInfoJson(this.crashDetectId);
        } else {
            // Get List of Crash Detects API
        	log.info("Get List of Feedbacks API");
        	jsonReturn = getListOfCrashDetectsJson();
        }
        
        return new JsonRepresentation(jsonReturn);
    }
    
    @Put("json")
    public JsonRepresentation put(Representation entity) {
        log.info("in put for Crash Detect resource");
        return new JsonRepresentation(updateCrashDetect(entity));
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
			
			if(json.has("instanceUrl")) {
				crashDetect.setInstanceUrl(json.getString("instanceUrl"));
			}
			
			// Default status to 'new'
			crashDetect.setStatus(CrashDetect.NEW_STATUS);

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
    
    private JSONObject getCrashDetectInfoJson(String theCrashDetectId) {
       	EntityManager em = EMF.get().createEntityManager();
    	JSONObject jsonReturn = new JSONObject();
    	
		String apiStatus = ApiStatusCode.SUCCESS;
		this.setStatus(Status.SUCCESS_OK);
		try {
			if (theCrashDetectId == null || theCrashDetectId.length() == 0) {
				apiStatus = ApiStatusCode.CRASH_DETECT_ID_REQUIRED;
				jsonReturn.put("apiStatus", apiStatus);
				return jsonReturn;
			}
			
			Key crashDetectKey = KeyFactory.stringToKey(theCrashDetectId);
    		CrashDetect crashDetect = null;
    		crashDetect = (CrashDetect)em.createNamedQuery("CrashDetect.getByKey")
				.setParameter("key", crashDetectKey)
				.getSingleResult();

    		jsonReturn.put("crashDetectId", KeyFactory.keyToString(crashDetect.getKey()));
			
        	Date detectedDate = crashDetect.getDetectedDate();
        	// TODO support time zones
        	if(detectedDate != null) jsonReturn.put("detectedDate", GMT.convertToLocalDate(detectedDate, null));
        	
        	jsonReturn.put("userName", crashDetect.getUserName());
        	jsonReturn.put("instanceUrl", crashDetect.getInstanceUrl());
        	
        	// TODO remove eventually, for backward compatibility before status field existed. If status not set, default to 'new'
        	String status = crashDetect.getStatus();
        	if(status == null || status.length() == 0) {status = "new";}
        	jsonReturn.put("status", status);
        	
        	String crashStackDataUrl = SandboxApplication.APPLICATION_BASE_URL + "crashStackData/" + theCrashDetectId + CrashStackDataServlet.FILE_EXT;
        	jsonReturn.put("crashStackDataUrl", crashStackDataUrl);
            log.info("JSON return object built successfully");	
		} catch (JSONException e) {
			log.severe("error converting json representation into a JSON object");
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
		} catch (NoResultException e) {
			// feedback ID passed in is not valid
			log.info("Crash Detect not found");
			apiStatus = ApiStatusCode.CRASH_DETECT_NOT_FOUND;
		} catch (NonUniqueResultException e) {
			log.severe("should never happen - two or more crash detects have same key");
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
    
    private JSONObject getListOfCrashDetectsJson() {
       	EntityManager em = EMF.get().createEntityManager();
    	JSONObject jsonReturn = new JSONObject();
    	
		String apiStatus = ApiStatusCode.SUCCESS;
		this.setStatus(Status.SUCCESS_OK);
		try {
			// cannot use a NamedQuery for a batch get of keys
			List<CrashDetect> crashDetects = (List<CrashDetect>)em.createNamedQuery("CrashDetect.getAll").getResultList();

			JSONArray crashDetectJsonArray = new JSONArray();
			for (CrashDetect cd : crashDetects) {
				JSONObject crashDetectJsonObj = new JSONObject();
				
				crashDetectJsonObj.put("crashDetectId", KeyFactory.keyToString(cd.getKey()));
				
            	Date detectedDate = cd.getDetectedDate();
            	// TODO support time zones
            	if(detectedDate != null) crashDetectJsonObj.put("detectedDate", GMT.convertToLocalDate(detectedDate, null));
            	
            	crashDetectJsonObj.put("userName", cd.getUserName());
            	crashDetectJsonObj.put("instanceUrl", cd.getInstanceUrl());
            	
            	// TODO remove eventually, for backward compatibility before status field existed. If status not set, default to 'new'
            	String status = cd.getStatus();
            	if(status == null || status.length() == 0) {status = "new";}
            	crashDetectJsonObj.put("status", status);
				
            	crashDetectJsonArray.put(crashDetectJsonObj);
			}
			jsonReturn.put("crashDetects", crashDetectJsonArray);
		} catch (JSONException e) {
			log.severe("error converting json representation into a JSON object");
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
		} catch (Exception e) {
			log.severe("getListOfCrashDetectsJson(): exception = " + e.getMessage());
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


    private JSONObject updateCrashDetect(Representation entity) {
        EntityManager em = EMF.get().createEntityManager();
    	JSONObject jsonReturn = new JSONObject();
    	
		String apiStatus = ApiStatusCode.SUCCESS;
		this.setStatus(Status.SUCCESS_OK);

		CrashDetect crashDetect = null;
        em.getTransaction().begin();
        try {
			if (this.crashDetectId == null || this.crashDetectId.length() == 0) {
				apiStatus = ApiStatusCode.CRASH_DETECT_ID_REQUIRED;
				jsonReturn.put("apiStatus", apiStatus);
				return jsonReturn;
			}
			
			crashDetect = new CrashDetect();
            JSONObject json = new JsonRepresentation(entity).getJsonObject();
            if (this.crashDetectId != null) {
                Key key = KeyFactory.stringToKey(this.crashDetectId);
                crashDetect = (CrashDetect)em.createNamedQuery("CrashDetect.getByKey")
                	.setParameter("key", key)
                	.getSingleResult();
            }
            if(json.has("status")) {
            	String status = json.getString("status");
            	if(crashDetect.isStatusValid(status)) {
            		crashDetect.setStatus(status);
            	} else {
            		apiStatus = ApiStatusCode.INVALID_STATUS;
            	}
            }
            em.persist(crashDetect);
            em.getTransaction().commit();
        } catch (IOException e) {
            log.severe("error extracting JSON object from Post");
            e.printStackTrace();
            this.setStatus(Status.SERVER_ERROR_INTERNAL);
        } catch (JSONException e) {
            e.printStackTrace();
            this.setStatus(Status.SERVER_ERROR_INTERNAL);
        } catch (NoResultException e) {
			// feedback ID passed in is not valid
			log.info("CrashDetect not found");
			apiStatus = ApiStatusCode.CRASH_DETECT_NOT_FOUND;
		} catch (NonUniqueResultException e) {
			log.severe("should never happen - two or more crash detects have same key");
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
			log.severe("error converting json representation into a JSON object");
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
		}

		return jsonReturn;
    }
}
