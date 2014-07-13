package tuwien.big.formel0.twitter;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test publishing to Twitter via the API
 *
 * @author pl
 *
 */
public class TwitterClientTest {

    @Test
    public void testPublishToTwitter() throws Exception {
        ITwitterClient client = new TwitterClient();
        TwitterStatusMessage message = new TwitterStatusMessage("User X", "The UUID", new Date(System.currentTimeMillis()));
        String publicationString = message.getTwitterPublicationString();
        System.out.println(publicationString);
        client.publishUuid(message);
    }
}
