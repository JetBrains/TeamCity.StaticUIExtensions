/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.staticUIExtensions;

import jetbrains.buildServer.controllers.AuthorizationInterceptor;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.controllers.BaseControllerTestCase;
import jetbrains.buildServer.controllers.MockRequest;
import jetbrains.buildServer.staticUIExtensions.web.StaticPageContentController;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.springframework.mock.web.MockServletContext;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import java.io.File;

@Test
public class StaticPageContentControllerTest extends BaseControllerTestCase {

  private AuthorizationInterceptor auth;
  private WebControllerManager web;
  private ServletContext context;


  @Override
  protected BaseController createController() {
    Mockery m = new Mockery();

    context = new MockServletContext();


    Configuration config = new Configuration() {
      @NotNull
      public File getConfigurationXml() {
        throw new UnsupportedOperationException();
      }

      @Nullable
      public File mapIncludeFilePath(@NotNull String path) {
        return null;
      }

      @NotNull
      public String getAccessToken() {
        throw new UnsupportedOperationException();
      }

      @NotNull
      public File getIncludeFilesBase() {
        return FileUtil.getCanonicalFile(new File("./tests/testData/"));
      }
    };
    web = m.mock(WebControllerManager.class);
    auth = m.mock(AuthorizationInterceptor.class);

    myRequest = new MockRequest() {
      public ServletContext getServletContext() {
        return context;
      }
    };


    m.checking(new Expectations() {{
      allowing(web);
      allowing(auth);
    }});

    return new StaticPageContentController(auth, web, config);
  }

  @Test
  public void testSimpleHtml() throws Exception {
    myRequest.setRequestURI("bs", "/app/static_content/w1/widget.html");
    doGet();
    assertEquals("text/html", myResponse.getContentType());
    String returnedContent = myResponse.getReturnedContent();
    assertTrue(returnedContent.contains("Static page content (from w1/widget.html)"));
  }

  @Test
  public void testJs() throws Exception {
    myRequest.setRequestURI("bs", "/app/static_content/w2/js/my.js");
    doGet();
    assertFalse(myResponse.getContentType().equals("text/html"));
    String returnedContent = myResponse.getReturnedContent();
    assertTrue(returnedContent.contains("alert('test alert');"));
  }

  @Test
  public void testRequestWithParameters() throws Exception {
    myRequest.setRequestURI("bs", "/app/static_content/w1/widget.html?p1=1&p2=2");
    doGet();
    String returnedContent = myResponse.getReturnedContent();
    assertTrue(returnedContent.contains("Static page content (from w1/widget.html)"));
  }

  @Test
  public void testWrongFileRequest() throws Exception {
    myRequest.setRequestURI("bs", "/app/static_content/w1/widget1.html");
    doGet();
    String returnedContent = myResponse.getReturnedContent();
    assertTrue(returnedContent.contains("ERROR: Content for StaticUIExtensions plugin was not found. Requested resource: w1/widget1.html"));
  }
}
