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
import jetbrains.buildServer.web.PagePlacesEx;
import jetbrains.buildServer.web.openapi.PlaceId;
import org.jmock.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 18:24
 */
public class PagePlacesCollectorTest extends BaseTestCase {
  private PagePlacesEx myPagePlaces;
  private Mock myMockPagePlaces;

  @BeforeMethod(alwaysRun = true)
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myMockPagePlaces = mock(PagePlacesEx.class);
    myPagePlaces = (PagePlacesEx) myMockPagePlaces.proxy();
  }

  @Test
  public void testListPagePlaces() {
    final Collection<PlaceId> placeIds = new PagePlacesCollector(myPagePlaces).getPlaceIds().values();
    Assert.assertTrue(placeIds.size() > 0);
    Assert.assertTrue(placeIds.contains(PlaceId.ALL_PAGES_FOOTER));
    Assert.assertTrue(placeIds.contains(PlaceId.ALL_PAGES_HEADER));
  }

  @Test
  public void testListPagePlacesExcludesTabs() {
    final Collection<PlaceId> placeIds = new PagePlacesCollector(myPagePlaces).getPlaceIds().values();
    Assert.assertTrue(placeIds.size() > 0);
    Assert.assertFalse(placeIds.contains(PlaceId.ADMIN_SERVER_CONFIGURATION_TAB));
    Assert.assertFalse(placeIds.contains(PlaceId.MY_TOOLS_TABS));
  }

  @Test
  public void testListPagePlacesIncludesRegisteredPlaceIds() {
    myMockPagePlaces.stubs().method("getRegisteredPlaceIds").will(returnValue(Arrays.asList(new PlaceId("id1"), new PlaceId("id2"))));

    PagePlacesCollector collector = new PagePlacesCollector(myPagePlaces);
    assertTrue(collector.getPlaceIds().containsKey("id1"));
    assertTrue(collector.getPlaceIds().containsKey("id2"));
  }
}
