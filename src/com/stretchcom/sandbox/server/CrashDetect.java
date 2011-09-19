package com.stretchcom.sandbox.server;

import java.util.Date;

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
    		name="CrashDetect.getAll",
    		query="SELECT cd FROM CrashDetect cd"
    ),
})
public class CrashDetect {
	private String summary;
	// TODO support time zone and GMT for dates
	private Date detectedDate;
	private String userName;
	private Text stackDataBase64;
	private String instanceUrl;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;

    public Key getKey() {
        return key;
    }

    public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Date getDetectedDate() {
		return detectedDate;
	}

	public void setDetectedDate(Date detectedDate) {
		this.detectedDate = detectedDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Text getStackDataBase64() {
		return stackDataBase64;
	}

	public void setStackDataBase64(String stackDataBase64) {
		this.stackDataBase64 = new Text(stackDataBase64);
	}

	public String getInstanceUrl() {
		return instanceUrl;
	}

	public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}
}
