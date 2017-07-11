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
import jetbrains.buildServer.staticUIExtensions.config.ConfigurationReader;
import jetbrains.buildServer.staticUIExtensions.config.ConfigurationReaderImpl;
import jetbrains.buildServer.staticUIExtensions.model.Rule;
import jetbrains.buildServer.web.openapi.PlaceId;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 19:17
 */
public class ConfigurationReaderTest extends BaseTestCase {
  private PagePlacesCollector myCollector;
  private ConfigurationReader myReader;

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myCollector = new PagePlacesCollector();
    myReader = new ConfigurationReaderImpl(myCollector);
  }

  @Test
  public void test_read_configuration_1() throws ConfigurationException {
    final Collection<Rule> rules = myReader.parseConfiguration(Paths.getConfigsFile("config_01.xml"));
    Assert.assertEquals(rules.size(), 4);

    System.out.println(rules);
  }

  @Test
  public void test_read_configuration_2_equals() throws ConfigurationException {
    final Collection<Rule> rules = myReader.parseConfiguration(Paths.getConfigsFile("config_02.xml"));
    Assert.assertEquals(rules.size(), 1);
    final Rule rule = rules.iterator().next();

    Assert.assertEquals(rule.getContent().getCSS(), null);
    Assert.assertEquals(rule.getContent().getHTML(), "header.html");
    Assert.assertEquals(rule.getContent().getJS(), null);

    Assert.assertEquals(rule.getPlace(), PlaceId.ALL_PAGES_HEADER);

    Assert.assertEquals(rule.getUrlMatcher().matches("overview.html"), true);
    Assert.assertEquals(rule.getUrlMatcher().matches("overview.html333"), false);
    Assert.assertEquals(rule.getUrlMatcher().matches("zzzoverview.html"), false);

    System.out.println(rules);
  }

  @Test
  public void test_read_configuration_3_starts() throws ConfigurationException {
    final Collection<Rule> rules = myReader.parseConfiguration(Paths.getConfigsFile("config_03.xml"));
    Assert.assertEquals(rules.size(), 1);
    final Rule rule = rules.iterator().next();

    Assert.assertEquals(rule.getContent().getCSS(), "header.css");
    Assert.assertEquals(rule.getContent().getHTML(), null);
    Assert.assertEquals(rule.getContent().getJS(), null);

    Assert.assertEquals(rule.getPlace(), PlaceId.ALL_PAGES_HEADER);

    Assert.assertEquals(rule.getUrlMatcher().matches("overview.html"), true);
    Assert.assertEquals(rule.getUrlMatcher().matches("overview.html333"), true);
    Assert.assertEquals(rule.getUrlMatcher().matches("zzzoverview.html"), false);

    System.out.println(rules);
  }

  @Test
  public void test_read_configuration_3_all() throws ConfigurationException {
    final Collection<Rule> rules = myReader.parseConfiguration(Paths.getConfigsFile("config_04.xml"));
    Assert.assertEquals(rules.size(), 1);
    final Rule rule = rules.iterator().next();

    Assert.assertEquals(rule.getContent().getCSS(), null);
    Assert.assertEquals(rule.getContent().getHTML(), null);
    Assert.assertEquals(rule.getContent().getJS(), "header.js");

    Assert.assertEquals(rule.getPlace(), PlaceId.ALL_PAGES_HEADER);

    Assert.assertEquals(rule.getUrlMatcher().matches("overview.html"), true);
    Assert.assertEquals(rule.getUrlMatcher().matches("overview.html333"), true);
    Assert.assertEquals(rule.getUrlMatcher().matches("zzzoverview.html"), true);
    Assert.assertEquals(rule.getUrlMatcher().matches(""), true);

    System.out.println(rules);
  }

  @Test
  public void test_read_configuration_5_all() throws ConfigurationException {
    final Collection<Rule> rules = myReader.parseConfiguration(Paths.getConfigsFile("config_05.xml"));
    Assert.assertEquals(rules.size(), 1);
    final Rule rule = rules.iterator().next();

    Assert.assertEquals(rule.getContent().getCSS(), null);
    Assert.assertEquals(rule.getContent().getHTML(), "header.html");
    Assert.assertEquals(rule.getContent().getJS(), null);

    Assert.assertEquals(rule.getPlace(), PlaceId.ALL_PAGES_HEADER);

    Assert.assertEquals(rule.getUrlMatcher().matches("project.html?projectId=id111&tab=projectOverview"), true);
    Assert.assertEquals(rule.getUrlMatcher().matches("project.html?projectId=id111&tab=projectOverview&guest=1"), true);
    Assert.assertEquals(rule.getUrlMatcher().matches("project.html?tab=projectOverview&projectId=id111"), false);
    Assert.assertEquals(rule.getUrlMatcher().matches(""), false);

    System.out.println(rules);
  }

  @Test
  public void test_read_configuration_6() throws ConfigurationException {
    final Collection<Rule> rules = myReader.parseConfiguration(Paths.getConfigsFile("config_06.xml"));
    Assert.assertEquals(rules.size(), 1);
    final Rule rule = rules.iterator().next();

    Assert.assertEquals(rule.getContent().getCSS(), "header.css");
    Assert.assertEquals(rule.getContent().getHTML(), null);
    Assert.assertEquals(rule.getContent().getJS(), null);

    Assert.assertEquals(rule.getPlace(), PlaceId.ALL_PAGES_HEADER);

    Assert.assertEquals(rule.getUrlMatcher().matches("project.html?projectId=id111&tab=projectOverview"), true);
    Assert.assertEquals(rule.getUrlMatcher().matches("project.html?projectId=id111&tab=projectOverview&guest=1"), true);
    Assert.assertEquals(rule.getUrlMatcher().matches("project.html?tab=projectOverview&projectId=id111"), true);
    Assert.assertEquals(rule.getUrlMatcher().matches(""), true);

    System.out.println(rules);
  }
}
