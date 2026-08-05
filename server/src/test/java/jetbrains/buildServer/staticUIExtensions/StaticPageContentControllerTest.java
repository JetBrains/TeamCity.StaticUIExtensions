
package jetbrains.buildServer.staticUIExtensions;

import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.controllers.*;
import jetbrains.buildServer.staticUIExtensions.web.StaticPageContentController;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.WebUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.springframework.mock.web.MockServletContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import java.io.File;
import java.lang.reflect.Constructor;

@Test
public class StaticPageContentControllerTest extends BaseControllerTestCase {

  private AuthorizationInterceptor auth;
  private WebControllerManager web;
  private ServletContext context;
  private long myRequestDate;


  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    ServerFixture.requireServerFixture();
    super.setUp();
  }


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
        return FileUtil.getCanonicalFile(new File("./testData/"));
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

    return new StaticPageContentController(auth, web, config, getHttpDownloadProcessor());
  }

  private HttpDownloadProcessor getHttpDownloadProcessor() {
    final Class<HttpDownloadProcessor> clazz = HttpDownloadProcessor.class;
    // TC 8.0: Constructor without parameters.
    try {
      final Constructor<HttpDownloadProcessor> constructor = clazz.getConstructor();
      return constructor.newInstance();
    } catch (Exception ignored) {
    }
    // TC 8.1 up to 2017.1
    try {
      final Class<?> dcc = HttpDownloadProcessor.class.getClassLoader().loadClass("jetbrains.buildServer.artifacts.DigestCalculator");
      final Constructor<HttpDownloadProcessor> constructor = clazz.getConstructor(dcc);

      final Class<?> sdcc = dcc.getClassLoader().loadClass("jetbrains.buildServer.artifacts.impl.SimpleDigestCalculator");
      final Object calculator = sdcc.newInstance();

      return constructor.newInstance(calculator);
    } catch (Exception ignored) {
    }
    // TC 2017.1
    try {
      final Class<?> dcc = HttpDownloadProcessor.class.getClassLoader().loadClass("jetbrains.buildServer.artifacts.DigestCalculator");
      final Class<?> epc = HttpDownloadProcessor.class.getClassLoader().loadClass("jetbrains.buildServer.ExtensionsProvider");
      final Constructor<HttpDownloadProcessor> constructor = clazz.getConstructor(dcc, epc);

      final Class<?> sdcc = dcc.getClassLoader().loadClass("jetbrains.buildServer.artifacts.impl.SimpleDigestCalculator");
      final Object calculator = sdcc.newInstance();

      Class<?> epcc = dcc.getClassLoader().loadClass("jetbrains.buildServer.serverSide.impl.BuildServerServiceLocator");
      final Object extensionProvider = epcc.newInstance();

      return constructor.newInstance(calculator, extensionProvider);
    } catch (Exception ignored) {
    }
    // It's not good.
    fail("This tests works with TeamCity 8.0, 8.1. Please check api of your TeamCity and this tests sources.");
    return null;
  }

  @BeforeMethod
  public void cleanup() {
    myRequestDate = -1;
  }

  @Test
  public void testSimpleHtml() throws Exception {
    myRequest.setRequestURI("bs", "/app/static_content/w1/widget.html");
    doGet();
    assertNotNull(myResponse.getContentType());
    assertNotNull(myResponse.getCharacterEncoding());
    assertNotNull(myResponse.getHeaders("Content-Length"));
    assertNotNull(myResponse.getHeader("ETag"));
    assertEquals("text/html", myResponse.getContentType());
    assertContains(myResponse.getReturnedContent(), "Static page content (from w1/widget.html)");
  }

  @Test
  public void testJs() throws Exception {
    myRequest.setRequestURI("bs", "/app/static_content/w2/js/my.js");
    doGet();
    String returnedContent = myResponse.getReturnedContent();
    assertTrue(returnedContent.contains("alert('test alert');"));
  }

  @Test
  public void testRequestWithParameters() throws Exception {
    myRequest.setRequestURI("bs", "/app/static_content/w1/widget.html?p1=1&p2=2");
    doGet();
    assertEquals(getHtmlMimeType(), myResponse.getContentType());
    assertContains(myResponse.getReturnedContent(), "Static page content (from w1/widget.html)");
  }

  @Test
  public void testRequestWithGuestAuth() throws Exception {
    myRequest.setRequestURI("bs", WebUtil.GUEST_AUTH_PREFIX + "app/static_content/w1/widget.html?p1=1&p2=2");
    doGet();
    assertEquals(getHtmlMimeType(), myResponse.getContentType());
    assertContains(myResponse.getReturnedContent(), "Static page content (from w1/widget.html)");
  }

  @Test
  public void testRequestWithHttpAuth() throws Exception {
    myRequest.setRequestURI("bs", WebUtil.HTTP_AUTH_PREFIX + "app/static_content/w1/widget.html?p1=1&p2=2");
    doGet();
    assertEquals(getHtmlMimeType(), myResponse.getContentType());
    assertContains(myResponse.getReturnedContent(), "Static page content (from w1/widget.html)");
  }

  @Test
  public void testWrongFileRequest() throws Exception {
    myRequest.setRequestURI("bs", "/app/static_content/w1/widget1.html");
    doGet();
    assertEquals(404, myResponse.getStatus());
    assertTrue(StringUtil.isEmpty(myResponse.getReturnedContent()));
  }

  @Test
  public void testNotModifiedRequest() throws Exception {
    myRequest.setRequestURI("bs", "/app/static_content/w1/widget.html");
    myRequestDate = System.currentTimeMillis() - 10;
    doGet();
    assertEquals(304, myResponse.getStatus());
    assertTrue(StringUtil.isEmpty(myResponse.getReturnedContent()));
  }

  @Test
  public void testIllegalAccess() throws Exception {
    myRequest.setRequestURI("bs", "/app/static_content/../w1/widget.html");
    doGet();
    assertEquals(404, myResponse.getStatus());
    assertTrue(StringUtil.isEmpty(myResponse.getReturnedContent()));
  }

  @Test
  public void testServletContextMimeType() throws Exception {
    assertEquals("text/html", getHtmlMimeType());
  }


  private String getHtmlMimeType() throws Exception {
    return context.getMimeType("widget.html");
  }

}