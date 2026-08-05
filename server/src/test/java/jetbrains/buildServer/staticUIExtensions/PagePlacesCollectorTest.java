

package jetbrains.buildServer.staticUIExtensions;

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.web.PagePlacesEx;
import jetbrains.buildServer.web.openapi.PlaceId;
import org.jmock.Mock;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
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
    try {
      PagePlacesEx.class.getMethod("getRegisteredPlaceIds");
    } catch (NoSuchMethodException e) {
      throw new SkipException("This test cannot be executed with this version of TeamCity");
    }

    myMockPagePlaces.stubs().method("getRegisteredPlaceIds").will(returnValue(Arrays.asList(new PlaceId("id1"), new PlaceId("id2"))));

    PagePlacesCollector collector = new PagePlacesCollector(myPagePlaces);
    assertTrue(collector.getPlaceIds().containsKey("id1"));
    assertTrue(collector.getPlaceIds().containsKey("id2"));
  }
}