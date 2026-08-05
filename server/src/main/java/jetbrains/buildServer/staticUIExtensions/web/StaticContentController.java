

package jetbrains.buildServer.staticUIExtensions.web;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.controllers.AuthorizationInterceptor;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.staticUIExtensions.Configuration;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.apache.commons.httpclient.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 19:47
 */
public class StaticContentController extends BaseController {
  private static final Logger LOG = Logger.getInstance(StaticContentController.class.getName());

  private ControllerPaths myPaths;
  private final Configuration myConfig;
  private final StaticContentCache myCache;
  private final ContentWrapper myCssWrapper = new ContentWrapper("\n<style type=\"text/css\">\n", "\n</style>\n");
  private final ContentWrapper myJsWrapper = new ContentWrapper("\n<script type=\"text/javascript\">\n", "\n</script>\n");
  private final ContentWrapper myHtmlWrapper = new ContentWrapper("", "");

  public StaticContentController(@NotNull final AuthorizationInterceptor auth,
                                 @NotNull final WebControllerManager web,
                                 @NotNull final ControllerPaths paths,
                                 @NotNull final Configuration config,
                                 @NotNull final StaticContentCache cache) {
    myPaths = paths;
    myConfig = config;
    myCache = cache;
    final String path = paths.getResourceControllerRegistrationBase();
    web.registerController(path, this);
    auth.addPathNotRequiringAuth(path);
    auth.addPathNotRequiringAuth("/login.jsp");
  }

  @Override
  protected ModelAndView doHandle(@NotNull final HttpServletRequest request,
                                  @NotNull final HttpServletResponse response) throws Exception {

    final String token = request.getParameter(myPaths.getTokenParameter());

    if (!myConfig.getAccessToken().equals(token)) {
      response.sendError(HttpStatus.SC_NOT_FOUND, "Path not found. Invalid access token");
      return null;
    }

    if (request.getParameter(myPaths.getEmptyContentParameter()) != null) {
      return null;
    }

    ModelAndView modelAndView = processFile(request.getParameter(myPaths.getIncludeCssFileParameter()), myCssWrapper, response);
    if (modelAndView != null) {
      return modelAndView;
    }

    modelAndView = processFile(request.getParameter(myPaths.getIncludeJsFileParameter()), myJsWrapper, response);
    if (modelAndView != null) {
      return modelAndView;
    }

    modelAndView = processFile(request.getParameter(myPaths.getIncludeFileParameter()), myHtmlWrapper, response);
    if (modelAndView != null) {
      return modelAndView;
    }

    return null;
  }

  private ModelAndView processFile(@Nullable String file,
                                   @NotNull ContentWrapper wrapper,
                                   @NotNull final HttpServletResponse response) throws IOException {

    if (StringUtil.isEmptyOrSpaces(file)){
      return null;
    }

    final File includeFile = myConfig.mapIncludeFilePath(file);
    if (includeFile != null) {
      final char[] data;
      try {
        data = myCache.getContent(includeFile);
        wrapper.wrap(response, data);
      } catch (IOException e) {
        LOG.warn("Failed to open file to include: " + includeFile + ", error: " + e.toString());
        return sendError(response, "Failed to open file: " + includeFile.getName());
      }
    }

    if (includeFile == null || !includeFile.isFile()) {
      LOG.warn("Failed to open file to include: " + (includeFile != null ? includeFile : file) + ".");
      return sendError(response, "Path not found: " + file);
    }

    return null;
  }

  @Nullable
  private ModelAndView sendError(@NotNull final HttpServletResponse response,
                                 @NotNull final String errorMessage) throws IOException {
    response.getWriter().write("ERROR: Content for StaticUIExtensions plugin was not found. " + errorMessage);
    return null;
  }

  private static class ContentWrapper {
    @NotNull
    private final String prefix;
    @NotNull
    private final String suffix;

    private ContentWrapper(@NotNull String prefix, @NotNull String suffix) {
      this.prefix = prefix;
      this.suffix = suffix;
    }

    public void wrap(@NotNull final HttpServletResponse response, char[] data) throws IOException {
      response.getWriter().write(prefix);
      response.getWriter().write(data);
      response.getWriter().write(suffix);
    }
  }


}