package de.sopra.passwordmanager.model;

import org.junit.Test;

import org.junit.Assert;

import java.util.Date;

public class BasePasswordTest{
	@Test
	public void getSetTest(){ 
		Date now= new Date();
		BasePassword bp= new BasePassword("pw",1,now);
		bp.setChangeReminderDays(2);
		Assert.assertEquals("retrieved value not equal to expected",new Integer(2), bp.getChangeReminderDays());
		Date now2= new Date();
		bp.setLastChanged(now2);
		Assert.assertEquals("retrieved value not equal to expected",now2, bp.getLastChanged());
		bp.setPassword("ABC");
		Assert.assertEquals("retrieved value not equal to expected","ABC", bp.getPassword());
		
		
		
	}	

}
