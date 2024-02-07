
package jetbrains.buildServer.staticUIExtensions;

import jetbrains.buildServer.controllers.AuthorizationInterceptor;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.controllers.BaseControllerTestCase;
import jetbrains.buildServer.controllers.MockRequest;
import jetbrains.buildServer.serverSide.CriticalErrors;
import jetbrains.buildServer.serverSide.MockServerPluginDescriptior;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.serverSide.impl.FileWatcherFactory;
import jetbrains.buildServer.staticUIExtensions.config.ConfigurationImpl;
import jetbrains.buildServer.staticUIExtensions.config.ConfigurationReader;
import jetbrains.buildServer.staticUIExtensions.config.ConfigurationReaderImpl;
import jetbrains.buildServer.staticUIExtensions.web.ControllerPaths;
import jetbrains.buildServer.staticUIExtensions.web.StaticContentCache;
import jetbrains.buildServer.staticUIExtensions.web.StaticContentController;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.web.PagePlacesEx;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mock;
import org.jmock.Mockery;
import org.springframework.mock.web.MockServletContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Collections;

@Test
public class StaticContentControllerTest extends BaseControllerTestCase {

  private AuthorizationInterceptor auth;
  private WebControllerManager web;
  private ServletContext context;
  private long myRequestDate;
  private ConfigurationReader myReader;
  private PagePlacesCollector myCollector;
  private ConfigurationImpl myConfig;


  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Mock mockPagePlaces = mock(PagePlacesEx.class);
    PagePlacesEx pagePlaces = (PagePlacesEx) mockPagePlaces.proxy();
    myCollector = new PagePlacesCollector(pagePlaces);
    myReader = new ConfigurationReaderImpl(myCollector);
  }


  @Override
  protected BaseController createController() {
    Mockery m = new Mockery();

    context = new MockServletContext();

    final ServerPaths serverPaths = new ServerPaths("serverPaths");
    myConfig = new ConfigurationImpl(serverPaths) {
      @NotNull
      public File getIncludeFilesBase() {
        return FileUtil.getCanonicalFile(new File("./tests/testData/test1"));
      }
    };
    web = m.mock(WebControllerManager.class);
    auth = m.mock(AuthorizationInterceptor.class);

    myRequest = new MockRequest() {
      public ServletContext getServletContext() {
        return context;
      }

      @Override
      public long getDateHeader(String string) {
        return myRequestDate;
      }
    };


    m.checking(new Expectations() {{
      allowing(web);
      allowing(auth);
    }});

    CriticalErrors errors = m.mock(CriticalErrors.class);
    FileWatcherFactory fileWatcherFactory = new FileWatcherFactory(serverPaths, errors);

    return new StaticContentController(auth, web, new ControllerPaths(new MockServerPluginDescriptior(), myConfig), myConfig, new StaticContentCache(fileWatcherFactory));
  }

  @BeforeMethod
  public void cleanup() {
    myRequestDate = -1;
  }

  @Test
  public void testIncludeCss() throws Exception {
    myRequest.setRequestURI("bs", "/overview.html");

    doGet("token", myConfig.getAccessToken(), "includeCssFile", "header.css");
    assertContains(myResponse.getReturnedContent(), "background-color: red;");
    assertContains(myResponse.getReturnedContent(), "<style type=\"text/css\">");
  }

  @Test
  public void testIncludeJs() throws Exception {
    myRequest.setRequestURI("bs", "/overview.html");

    doGet("token", myConfig.getAccessToken(), "includeJsFile", "main.js");
    assertContains(myResponse.getReturnedContent(), "alert('hi from main.js');");
    assertContains(myResponse.getReturnedContent(), "<script type=\"text/javascript\">");
  }


  @Test
  public void testIncludeHtml() throws Exception {
    myRequest.setRequestURI("bs", "/overview.html");
    doGet("token", myConfig.getAccessToken(), "includeFile", "part.html");
    assertContains(myResponse.getReturnedContent(), "contented included by static ui plugin");
    assertNotContains(myResponse.getReturnedContent(), "<script type=\"text/javascript\">", false);
  }

  @Test
  public void testIncludeEmptyHtml() throws Exception {
    myRequest.setRequestURI("bs", "/overview.html");
    doGet("token", myConfig.getAccessToken(), "includeFile", "");
    assertNotContains(myResponse.getReturnedContent(), "ERROR: Content for StaticUIExtensions plugin was not found. No file specified.", false);
  }


  @Test
  public void testIncludeWrongFileName() throws Exception {
    myRequest.setRequestURI("bs", "/overview.html");
    doGet("token", myConfig.getAccessToken(), "includeFile", "no_file.html");
    assertContains(myResponse.getReturnedContent(), "ERROR: Content for StaticUIExtensions plugin was not found. Failed to open file: no_file.html");
  }


  @Test
  public void testIncludeEmptyHtmlAndCss() throws Exception {
    myRequest.setRequestURI("bs", "/overview.html");
    doGet("token", myConfig.getAccessToken(), "includeFile", "", "includeCssFile", "header.css");
    assertContains(myResponse.getReturnedContent(), "background-color: red;");
    assertContains(myResponse.getReturnedContent(), "<style type=\"text/css\">");
  }


}