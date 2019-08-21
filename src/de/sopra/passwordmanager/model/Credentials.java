package de.sopra.passwordmanager.model;

import java.net.URL;
import java.util.Collection;

public class Credentials extends BasePassword {

	private String name;

	private String userName;

	private URL website;

	private String notes;

	private long created;

	private Collection<SecurityQuestion> securityQuestions;

}
