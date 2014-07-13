/*
 * Copyright 2013 BIG TU Wien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.ac.tuwien.big.testsuite.impl.util;

import at.ac.tuwien.big.testsuite.api.task.EventHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class URLStreamHandlerFactoryImplTest {

    private static final String DEFAULT_OAUTH_REQUEST_TOKEN_URL = "http://api.twitter.com/oauth/request_token";
    private static final String DEFAULT_OAUTH_AUTHORIZATION_URL = "http://api.twitter.com/oauth/authorize";
    private static final String DEFAULT_OAUTH_ACCESS_TOKEN_URL = "http://api.twitter.com/oauth/access_token";
    private static final String DEFAULT_OAUTH_AUTHENTICATION_URL = "http://api.twitter.com/oauth/authenticate";

    private static final String DEFAULT_REST_BASE_URL = "http://api.twitter.com/1.1/";
    private static final String DEFAULT_STREAM_BASE_URL = "https://stream.twitter.com/1.1/";
    private static final String DEFAULT_USER_STREAM_BASE_URL = "https://userstream.twitter.com/1.1/";
    private static final String DEFAULT_SITE_STREAM_BASE_URL = "https://sitestream.twitter.com/1.1/";
    
    private static String albumUrl = "https://picasaweb.google.com/data/feed/api/user/107302466601293793664";
    
    @Test
    public void testCreateURLStreamHandler() throws Exception {
        final URLStreamHandlerFactoryImpl factory = URLStreamHandlerFactoryImpl.getInstance();
        final List<URL> accessedUrls = new ArrayList<>();
        factory.addUnhandeledURLEvenetHandler(new EventHandler<URL>() {

            @Override
            public void handle(URL event) {
                accessedUrls.add(event);
            }
        });
        HttpURLConnection connection = (HttpURLConnection) new URL("http://google.com").openConnection();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            System.out.println(br.readLine());
        } finally {
            connection.disconnect();
        }
        
        assertFalse(accessedUrls.isEmpty());
        assertEquals("google.com", accessedUrls.get(0).getHost());
    }
}