package com.stretchcom.sandbox.server;

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
}
