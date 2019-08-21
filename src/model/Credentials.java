package Model;

import java.util.Collection;

public class Credentials extends BasePassword {

	private String name;

	private String userName;

	private URL website;

	private String notes;

	private final long created;

	private Collection<SecurityQuestion> securityQuestions;

}
