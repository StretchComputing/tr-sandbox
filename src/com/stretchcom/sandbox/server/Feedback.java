package com.stretchcom.sandbox.server;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

@Entity
@NamedQueries({
    @NamedQuery(
    		name="Feedback.getAll",
    		query="SELECT fb FROM Feedback fb"
    ),
})
public class Feedback {
	@Basic private Text voiceBase64;
	private Date recordedDate;
	private String userName;
	private String instanceUrl;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;

    public Key getKey() {
        return key;
    }

	public String getVoiceBase64() {
		return this.voiceBase64 == null? null : this.voiceBase64.getValue();
	}

	public void setVoiceBase64(String voiceBase64) {
		this.voiceBase64 = new Text(voiceBase64);
	}
	
	public Date getRecordedDate() {
		return recordedDate;
	}

	public void setRecordedDate(Date recordedDate) {
		this.recordedDate = recordedDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getInstanceUrl() {
		return instanceUrl;
	}

	public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}
}
