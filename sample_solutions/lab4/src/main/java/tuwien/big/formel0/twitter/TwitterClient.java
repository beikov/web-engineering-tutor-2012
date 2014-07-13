package tuwien.big.formel0.twitter;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Used to access the REST API of Twitter
 *
 * @author pl
 *
 */
public class TwitterClient implements ITwitterClient {

    private static final String TOKEN = "1366513208-MutXEbBMAVOwrbFmZtj1r4Ih2vcoHGHE2207002";
    private static final String TOKEN_SECRET = "RMPWOePlus3xtURWRVnv1TgrjTyK7Zk33evp4KKyA";
    private static final String CONSUMERKEY = "GZ6tiy1XyB9W0P4xEJudQ";
    private static final String CONSUMERSECRET = "gaJDlW0vf7en46JwHAOkZsTHvtAiZ3QUd2mD1x26J9w";
    private static Twitter twitter;

    static {
        // Setup a connection to Twitter
        TwitterFactory factory = new TwitterFactory();
        AccessToken accessToken = new AccessToken(TOKEN, TOKEN_SECRET);
        twitter = factory.getInstance();
        twitter.setOAuthConsumer(CONSUMERKEY, CONSUMERSECRET);
        twitter.setOAuthAccessToken(accessToken);

    }

    /**
     * Publish a given uuid to twitter feed
     */
    public void publishUuid(TwitterStatusMessage message) throws Exception {
        twitter.updateStatus(message.getTwitterPublicationString());
    }
}
