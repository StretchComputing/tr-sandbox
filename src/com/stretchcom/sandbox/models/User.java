package com.stretchcom.sandbox.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.google.appengine.api.datastore.Key;

@Entity
@NamedQueries({ @NamedQuery(name = "User.getAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.getByEmailAddress", query = "SELECT u FROM User u WHERE u.emailAddress = :emailAddress") })
public class User {
    private String firstName;
    private String lastName;
    private String emailAddress;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;

    public User() {

    }

    public User(String firstName, String lastName, String emailAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
    }

    public Key getKey() {
        return key;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String theEmailAddress) {
        // always store email in lower case to make queries and comparisons case
        // insensitive
        if (theEmailAddress != null) {
            theEmailAddress = theEmailAddress.toLowerCase();
        }
        this.emailAddress = theEmailAddress;
    }
}
