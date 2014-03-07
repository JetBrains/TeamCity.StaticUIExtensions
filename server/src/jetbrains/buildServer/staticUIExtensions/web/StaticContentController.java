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

    //TODO: allow directories, may use Util from TeamCity for that
    final String file = request.getParameter(myPaths.getIncludeFileParameter());
    if (StringUtil.isEmptyOrSpaces(file) || file.contains("/") || file.contains("\\") || file.contains("..")) {
      final String message = "Failed to open file to include by invalid path: " + (file == null ? "no file specified" : file) + ".";
      LOG.warn(message);
      return sendError(request, response, message);
    }

    final File includeFile = myConfig.mapIncludeFilePath(file);
    if (includeFile == null || !includeFile.isFile()) {
      LOG.warn("Failed to open file to include: " + (includeFile != null ? includeFile : file) + ".");
      return sendError(request, response, "Path not found: " + file);
    }

    final char[] data;
    try {
      data = myCache.getContent(includeFile);
    } catch (IOException e) {
      LOG.warn("Failed to open file to include: " + includeFile + ". " + e.getMessage(), e);
      return sendError(request, response, "Failed to open file: " + includeFile.getName());
    }

    response.getWriter().write(data);
    return null;
  }

  @Nullable
  private ModelAndView sendError(@NotNull final HttpServletRequest request,
                                 @NotNull final HttpServletResponse response,
                                 @NotNull final String errorMessage) throws IOException {
    response.getWriter().write("ERROR: Content for StaticUIExtensions plugin was not found. " + errorMessage);
    return null;
  }
}
