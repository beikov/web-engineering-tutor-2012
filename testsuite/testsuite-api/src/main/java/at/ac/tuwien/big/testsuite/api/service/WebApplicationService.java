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
package at.ac.tuwien.big.testsuite.api.service;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

/**
 *
 * @author Christian
 */
public interface WebApplicationService {

    public String deployWar(File warFile);

    public String deployWar(File warFile, String contextPath);

    public String deployWar(InputStream warFileStream);

    public String deployWar(InputStream warFileStream, String contextPath);

    public String deployWebapp(File docRoot);

    public String deployWebapp(File docRoot, String contextPath);

    public Collection<Throwable> undeploy(String contextPath);

    public String getWelcomeFileUrl(String contextPath);

    public String getUserServletUrl(String contextPath);

    public String getServerBase(String contextPath);
}
