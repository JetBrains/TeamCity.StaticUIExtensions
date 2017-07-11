/*
 * Copyright 2000-2011 JetBrains s.r.o.
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

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.staticUIExtensions.model.Rule;
import jetbrains.buildServer.staticUIExtensions.model.StartsWithMatcher;
import jetbrains.buildServer.staticUIExtensions.model.StaticContent;
import jetbrains.buildServer.staticUIExtensions.model.TrueMatcher;
import jetbrains.buildServer.staticUIExtensions.web.ControllerPaths;
import jetbrains.buildServer.staticUIExtensions.web.RulePageExtension;
import jetbrains.buildServer.staticUIExtensions.web.RulePageExtensionsFactory;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 17.11.11 17:54
 */
public class RulePageExtensionTest extends BaseTestCase {
  private Mockery m;
  private PluginDescriptor myDescriptor;
  private Configuration myConfig;
  private RulePageExtensionsFactory myFactory;
  private PagePlaces myPagePlaces;

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    m = new Mockery();
    myDescriptor = m.mock(PluginDescriptor.class);
    myConfig = m.mock(Configuration.class);
    myPagePlaces = m.mock(PagePlaces.class);

    m.checking(new Expectations(){{
      allowing(myDescriptor).getPluginResourcesPath(with(any(String.class))); will(new CustomAction("map plugin resources") {
        public Object invoke(Invocation invocation) throws Throwable {
          return "/base/" + invocation.getParameter(0);
        }
      });
      allowing(myDescriptor).getPluginName(); will(returnValue("pluginName"));
      allowing(myConfig).getAccessToken();will(returnValue("token"));
    }});

    myFactory = new RulePageExtensionsFactory(myPagePlaces, myDescriptor, new ControllerPaths(myDescriptor, myConfig));



  }

  @NotNull
  private RulePageExtension create(@NotNull final Rule rule) {
    return myFactory.createExtension(rule);
  }


  @Test
  public void testHTMLResource2() {
    Rule r = new Rule(
            "aaa",
            new TrueMatcher(),
            PlaceId.ALL_PAGES_HEADER,
            new StaticContent(null, null, null));

    final RulePageExtension ext = create(r);

    Assert.assertEquals(ext.getCssPaths(), Collections.emptyList());
    Assert.assertEquals(ext.getJsPaths(), Collections.emptyList());
    Assert.assertEquals(ext.getIncludeUrl(), "/base/resources.html?token=token&showEmptyContent=42");
  }


  @Test
  public void testCSSResource() {
    Rule r = new Rule(
            "aaa",
            new TrueMatcher(),
            PlaceId.ALL_PAGES_HEADER,
            new StaticContent(null, null, "header.css"));

    final RulePageExtension ext = create(r);

    Assert.assertEquals(ext.getIncludeUrl(), "/base/resources.html?token=token&includeCssFile=header.css");
    Assert.assertEquals(ext.getCssPaths(), Collections.emptyList());
    Assert.assertEquals(ext.getJsPaths(), Collections.emptyList());
  }


  @Test
  public void testJSResource() {
    Rule r = new Rule(
            "aaa",
            new TrueMatcher(),
            PlaceId.ALL_PAGES_HEADER,
            new StaticContent(null, "main.js", null));

    final RulePageExtension ext = create(r);

    Assert.assertEquals(ext.getIncludeUrl(), "/base/resources.html?token=token&includeJsFile=main.js");
    Assert.assertEquals(ext.getCssPaths(), Collections.emptyList());
    Assert.assertEquals(ext.getJsPaths(), Collections.emptyList());
  }

  @Test
  public void testHTMLResource() {
    Rule r = new Rule(
            "aaa",
            new TrueMatcher(),
            PlaceId.ALL_PAGES_HEADER,
            new StaticContent("main.html", "main.js", "main.css"));

    final RulePageExtension ext = create(r);

    Assert.assertEquals(ext.getIncludeUrl(), "/base/resources.html?token=token&includeFile=main.html&includeJsFile=main.js&includeCssFile=main.css");
  }

  @Test
  public void testEmptyHtmlWithCssAndJS() {
    Rule r = new Rule(
            "aaa",
            new TrueMatcher(),
            PlaceId.ALL_PAGES_HEADER,
            new StaticContent(null, "main.js", "main.css"));

    final RulePageExtension ext = create(r);

    Assert.assertEquals(ext.getIncludeUrl(), "/base/resources.html?token=token&includeJsFile=main.js&includeCssFile=main.css");
  }

  @Test
  public void testRequestMatch() {
    Rule r = new Rule(
            "aaa",
            new StartsWithMatcher("project.html?projectId=id1&tab=projectOverview"),
            PlaceId.ALL_PAGES_HEADER,
            new StaticContent(null, null, null));

    final RulePageExtension ext = create(r);

    Assert.assertTrue(ext.isAvailable(getMockRequest("project.jsp", "projectId=id1&tab=projectOverview", "")));
    Assert.assertFalse(ext.isAvailable(getMockRequest("project.jsp", "projectId=id2&tab=projectOverview", "")));
    Assert.assertFalse(ext.isAvailable(getMockRequest("project.jsp", "tab=projectOverview&projectId=id2", "")));
  }

  private HttpServletRequest getMockRequest(final String requestURI, final String quesryString, final String contextPath) {
    final Mockery mockery = new Mockery();
    final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
    mockery.checking(new Expectations() {{
      allowing(request).getRequestURI();  will(returnValue(requestURI));
      allowing(request).getQueryString();  will(returnValue(quesryString));
      allowing(request).getContextPath();  will(returnValue(contextPath));
    }}
  );
    return request;
  }
}
