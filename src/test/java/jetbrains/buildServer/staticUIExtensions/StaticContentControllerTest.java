/*
 * Copyright 2000-2015 JetBrains s.r.o.
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

import org.testng.annotations.Test;

import java.io.File;

import static jetbrains.buildServer.TeamCityAsserts.assertContains;
import static jetbrains.buildServer.TeamCityAsserts.assertNotContains;

@Test
public class StaticContentControllerTest extends ContentControllerTestBase {
  @Override
  protected File getIncludeFilesBase() {
    return new File("./tests/testData/test1");
  }

  @Test
  public void testIncludeCss() throws Exception {
    setRequestURI("bs", "/overview.html", "includeCssFile", "header.css");

    doGet();
    assertContains(myResponse.getContentAsString(), "background-color: red;");
    assertContains(myResponse.getContentAsString(), "<style type=\"text/css\">");
  }

  @Test
  public void testIncludeJs() throws Exception {
    setRequestURI("bs", "/overview.html", "includeJsFile", "main.js");

    doGet();
    assertContains(myResponse.getContentAsString(), "alert('hi from main.js');");
    assertContains(myResponse.getContentAsString(), "<script type=\"text/javascript\">");
  }


  @Test
  public void testIncludeHtml() throws Exception {
    setRequestURI("bs", "/overview.html", "includeFile", "part.html");
    doGet();
    assertContains(myResponse.getContentAsString(), "contented included by static ui plugin");
    assertNotContains(myResponse.getContentAsString(), "<script type=\"text/javascript\">", false);
  }

  @Test
  public void testIncludeEmptyHtml() throws Exception {
    setRequestURI("bs", "/overview.html", "includeFile", "");
    doGet();
    assertNotContains(myResponse.getContentAsString(), "ERROR: Content for StaticUIExtensions plugin was not found. No file specified.", false);
  }


  @Test
  public void testIncludeWrongFileName() throws Exception {
    setRequestURI("bs", "/overview.html", "includeFile", "no_file.html");
    doGet();
    assertContains(myResponse.getContentAsString(), "ERROR: Content for StaticUIExtensions plugin was not found. Failed to open file: no_file.html");
  }


  @Test
  public void testIncludeEmptyHtmlAndCss() throws Exception {
    setRequestURI("bs", "/overview.html", "includeFile", "", "includeCssFile", "header.css");
    doGet();
    assertContains(myResponse.getContentAsString(), "background-color: red;");
    assertContains(myResponse.getContentAsString(), "<style type=\"text/css\">");
  }
}
