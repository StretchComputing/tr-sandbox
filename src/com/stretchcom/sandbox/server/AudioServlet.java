package com.stretchcom.sandbox.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.data.Status;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.com.google.common.util.Base64;
import com.google.appengine.repackaged.com.google.common.util.Base64DecoderException;

public class AudioServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(AudioServlet.class.getName());
	private static int MAX_TASK_RETRY_COUNT = 3;

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("AudioServlet.doPost() entered - SHOULD NOT BE CALLED!!!!!!!!!!!!!!!!!");
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("AudioServlet.doGet() entered");
		ServletOutputStream out = null;
		// TODO confirm with Terry the mime type
		resp.setContentType("audio/mp4");

		try {
			String feedbackId = this.getFeedbackId(req);
			if (feedbackId == null) {
				log.info("could not extract feedbackID from URL");
				return;
			}

			byte[] voice = getFeedbackAudio(feedbackId);
			if (voice == null)
				return;

			out = resp.getOutputStream();
			out.write(voice);
		} catch (Exception e) {
			log.info("Servlet exception = " + e.getMessage());
			resp.setStatus(HttpServletResponse.SC_OK);
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}
	
	// extracts feedback id from URL
	// returns feedbackID or null if error
	private String getFeedbackId(HttpServletRequest theReq) {
		// extract the feedback ID from the URL (for http://hostname.com/mywebapp/servlet/MyServlet/a/b;c=123?d=789, returns /a/b;c=123
		String pathInfo = theReq.getPathInfo();
		log.info("get Audio URL pathInfo = " + pathInfo);
		if(pathInfo == null || pathInfo.length() == 0) {return null;}
		if(pathInfo.startsWith("/")) {
			pathInfo = pathInfo.substring(1);
		}
		return pathInfo;
	}
	
	// returns base64 decoded audio data of the specified feedback record if successful; null otherwise.
	private byte[] getFeedbackAudio(String theFeedbackId) {
		byte[] rawAudio = null;
		
		// using the feedbackID, retrieve the appropriate feedback record
       	EntityManager em = EMF.get().createEntityManager();
		try {
			Key feedbackKey = KeyFactory.stringToKey(theFeedbackId);
    		Feedback feedback = null;
    		feedback = (Feedback)em.createNamedQuery("Feedback.getByKey")
				.setParameter("key", feedbackKey)
				.getSingleResult();
    		
    		rawAudio = Base64.decode(feedback.getVoiceBase64());    		
		} catch (NoResultException e) {
			// feedback ID passed in is not valid
			log.info("Feedback ID not found");
		} catch (NonUniqueResultException e) {
			log.severe("should never happen - two or more feedback have same key");
		} catch (Base64DecoderException e) {
			log.severe("base64 decode exception = " + e.getMessage());
			e.printStackTrace();
		} 
		
		return rawAudio;
	}
}
