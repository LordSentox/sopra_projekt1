package Model;

import java.util.ArrayList;

public class CredentialsController {

	private PasswordManagerController passwordManagerController;

	public void saveCredentials(Credentials oldCredentials, Credentials newCredentials) {

	}

	public void removeCredentials(Credentials credentials) {

	}

	public void addSecurityQuestion(String question, String answer, Credentials credentials) {

	}

	public void removeSecurityQuestion(SecurityQuestion securityQuestion, Credentials credentials) {

	}

	public void filterCredentials(String categoryPath, String pattern) {

	}

	public void copyPasswordToClipboard(Credentials credentials) {

	}

	public void setPasswordShown(Credentials credentials, boolean visible) {

	}

	ArrayList getCredentialsByCategoryName(String categoryPath) {
		return null;
	}

	void clearPasswordFromClipboard(Credentials credentials) {

	}

	void reencryptAll(String oldMasterPassword, String newMasterPassword) {

	}

}
