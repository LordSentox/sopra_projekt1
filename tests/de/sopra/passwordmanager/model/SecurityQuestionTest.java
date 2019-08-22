package de.sopra.passwordmanager.model;

import org.junit.Assert;
import org.junit.Test;


public class SecurityQuestionTest {

	@Test
	public void getSetTest() {
		SecurityQuestion sq= new SecurityQuestion("abc?","def");
		Assert.assertEquals("retrieved value not equal to expected","abc?",sq.getQuestion());
		Assert.assertEquals("retrieved value not equal to expected","def",sq.getAnswer());		
		SecurityQuestion sf= new SecurityQuestion("abc?","def");
		SecurityQuestion sfn= new SecurityQuestion("def","abc?");
		Assert.assertTrue(sq.equals(sf));
		Assert.assertFalse(sq.equals(sfn));
		Assert.assertFalse(sq.equals(null));
		
		
	}
	
}
