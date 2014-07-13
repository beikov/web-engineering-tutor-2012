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

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

/**
 *
 * @author Christian
 */
public class DomResponseHandler implements ResponseHandler<Document> {

    private static final int MAX_RETRIES = 10;
    private final HttpClient httpClient;
    private final HttpRequestBase request;
    private int retries = 0;

    public DomResponseHandler(HttpClient httpClient, HttpRequestBase request) {
        this.httpClient = httpClient;
        this.request = request;
    }

    @Override
    public Document handleResponse(final HttpResponse response)
            throws HttpResponseException, IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        Document doc = null;

        if (statusLine.getStatusCode() >= 300) {

            if (statusLine.getStatusCode() == 502 && retries < MAX_RETRIES) {
                // Proxy Error
                retries++;
                doc = httpClient.execute(request, this);
                retries = 0;
            } else {
                EntityUtils.consume(entity);
                throw new HttpResponseException(statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
            }
        }

        if (doc == null) {
            doc = entity == null ? null : DomUtils.createDocument(entity.getContent());
        }

        request.releaseConnection();
        return doc;
    }
}
