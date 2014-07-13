package tuwien.big.formel0.highscore;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/*
 * Test high score publisher
 */
public class TestHighScorePublisher {

	
	/**
	 * Publish the highscore result
	 */
	@Test
	public void testHighScoreResult() {
		
		
		Assert.assertTrue(true);
		
		
		//Leave commented out as service is not always available until the assignment for lab 4 is online
		
		
//		PublishHighScore service = new PublishHighScore();
//		
//		try {
//			String uuid = service.publishHighScore("Dummy", new Date(), "MALE", "The computer", 123);
//			
//			System.out.println("Received UUID " + uuid);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			Assert.fail();
//		}
		
	}
	
	
	
}
