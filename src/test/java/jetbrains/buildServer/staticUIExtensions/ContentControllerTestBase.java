package jetbrains.buildServer.staticUIExtensions;

import jetbrains.buildServer.controllers.AuthorizationInterceptor;
import jetbrains.buildServer.staticUIExtensions.web.DownloadProcessor;
import jetbrains.buildServer.staticUIExtensions.web.StaticPageContentController;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

import static org.testng.Assert.assertNotNull;

public abstract class ContentControllerTestBase {
  private Mockery m;
  private AuthorizationInterceptor auth;
  private WebControllerManager web;

  protected ServletContext context;
  protected long myRequestDate;
  protected MockHttpServletRequest myRequest;
  protected MockHttpServletResponse myResponse;
  private StaticPageContentController myController;

  protected abstract File getIncludeFilesBase();

  @BeforeMethod
  protected void createController() {
    m = new Mockery();
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
        return FileUtil.getCanonicalFile(ContentControllerTestBase.this.getIncludeFilesBase());
      }
    };
    web = m.mock(WebControllerManager.class);
    auth = m.mock(AuthorizationInterceptor.class);

    myResponse = new MockHttpServletResponse();
    myRequest = new MockHttpServletRequest() {
      public ServletContext getServletContext() {
        return context;
      }

      @Override
      public long getDateHeader(String string) {
        return myRequestDate;
      }
    };


    final DownloadProcessor downloadProcessor = new DownloadProcessor() {
      @Nullable
      @Override
      public ModelAndView processFileDownload(@NotNull final File file,
                                              final boolean contentDisposition,
                                              @NotNull final HttpServletRequest request,
                                              @NotNull final HttpServletResponse response) {
        myResponse.setContentType(context.getMimeType(file.getName()));
        myResponse.setHeader("Content-Length", "42");
        myResponse.setHeader("ETag", "etag-for-test");
        return null;
      }
    };

    m.checking(new Expectations() {{
      allowing(web);
      allowing(auth);
    }});

    myController = new StaticPageContentController(auth, web, config, downloadProcessor);
    myRequestDate = -1;
  }

  protected void setRequestURI(String context, String uri, @NotNull String... params) {
    myRequest.setRequestURI(uri);
    myRequest.setContextPath(context);
    for(int i = 0; i < params.length;) {
      myRequest.setParameter(params[i++], params[i++]);
    }
  }

  protected void doGet() throws Exception {
    myRequest.setMethod("GET");
    myController.handleRequest(myRequest, myResponse);
  }
}
