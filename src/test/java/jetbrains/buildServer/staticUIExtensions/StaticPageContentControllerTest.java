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

import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.web.util.WebUtil;
import org.testng.annotations.Test;

import java.io.File;

import static jetbrains.buildServer.TeamCityAsserts.assertContains;
import static org.testng.Assert.*;

@Test
public class StaticPageContentControllerTest extends ContentControllerTestBase {
  @Test
  public void testSimpleHtml() throws Exception {
    setRequestURI("bs", "/app/static_content/w1/widget.html");
    doGet();
    assertNotNull(myResponse.getContentType());
    assertNotNull(myResponse.getCharacterEncoding());
    assertNotNull(myResponse.getHeaders("Content-Length"));
    assertNotNull(myResponse.getHeader("ETag"));
    assertEquals("text/html", myResponse.getContentType());
    assertContains(myResponse.getContentAsString(), "Static page content (from w1/widget.html)");
  }

  @Test
  public void testJs() throws Exception {
    setRequestURI("bs", "/app/static_content/w2/js/my.js");
    doGet();
    String returnedContent = myResponse.getContentAsString();
    assertTrue(returnedContent.contains("alert('test alert');"));
  }

  @Test
  public void testRequestWithParameters() throws Exception {
    setRequestURI("bs", "/app/static_content/w1/widget.html?p1=1&p2=2");
    doGet();
    assertEquals(getHtmlMimeType(), myResponse.getContentType());
    assertContains(myResponse.getContentAsString(), "Static page content (from w1/widget.html)");
  }

  @Test
  public void testRequestWithGuestAuth() throws Exception {
    setRequestURI("bs", WebUtil.GUEST_AUTH_PREFIX + "app/static_content/w1/widget.html?p1=1&p2=2");
    doGet();
    assertEquals(getHtmlMimeType(), myResponse.getContentType());
    assertContains(myResponse.getContentAsString(), "Static page content (from w1/widget.html)");
  }

  @Test
  public void testRequestWithHttpAuth() throws Exception {
    setRequestURI("bs", WebUtil.HTTP_AUTH_PREFIX + "app/static_content/w1/widget.html?p1=1&p2=2");
    doGet();
    assertEquals(getHtmlMimeType(), myResponse.getContentType());
    assertContains(myResponse.getContentAsString(), "Static page content (from w1/widget.html)");
  }

  @Test
  public void testWrongFileRequest() throws Exception {
    setRequestURI("bs", "/app/static_content/w1/widget1.html");
    doGet();
    assertEquals(404, myResponse.getStatus());
    assertTrue(StringUtil.isEmpty(myResponse.getContentAsString()));
  }

  @Test
  public void testNotModifiedRequest() throws Exception {
    setRequestURI("bs", "/app/static_content/w1/widget.html");
    myRequestDate = System.currentTimeMillis() - 10;
    doGet();
    assertEquals(304, myResponse.getStatus());
    assertTrue(StringUtil.isEmpty(myResponse.getContentAsString()));
  }

  @Test
  public void testIllegalAccess() throws Exception {
    setRequestURI("bs", "/app/static_content/../w1/widget.html");
    doGet();
    assertEquals(404, myResponse.getStatus());
    assertTrue(StringUtil.isEmpty(myResponse.getContentAsString()));
  }

  @Test
  public void testServletContextMimeType() throws Exception {
    assertEquals("text/html", getHtmlMimeType());
  }


  private String getHtmlMimeType() throws Exception {
    return context.getMimeType("widget.html");
  }

  @Override
  protected File getIncludeFilesBase() {
    return new File("./tests/testData/");
  }
}
