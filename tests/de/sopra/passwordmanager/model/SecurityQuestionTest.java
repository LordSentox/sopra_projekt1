package de.sopra.passwordmanager.model;

import de.sopra.passwordmanager.util.EncryptedString;
import org.junit.Assert;
import org.junit.Test;


public class SecurityQuestionTest {

	@Test
	public void getSetTest() {
		EncryptedString encryptedString1 = new EncryptedString("abc");
		EncryptedString encryptedString2 = new EncryptedString("def");
		SecurityQuestion sq= new SecurityQuestion(encryptedString1,encryptedString2);
		Assert.assertEquals("retrieved value not equal to expected", encryptedString1, sq.getQuestion());
		Assert.assertEquals("retrieved value not equal to expected", encryptedString2, sq.getAnswer());
		SecurityQuestion sf= new SecurityQuestion(encryptedString1,encryptedString2);
		SecurityQuestion sfn= new SecurityQuestion(encryptedString2,encryptedString1);
		Assert.assertEquals(sq, sf);
		Assert.assertNotEquals(sq, sfn);
		Assert.assertNotNull( sq);
	}
	
}
