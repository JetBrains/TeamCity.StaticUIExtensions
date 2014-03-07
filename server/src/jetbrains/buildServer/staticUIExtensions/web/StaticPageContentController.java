/*
 * Copyright 2000-2014 JetBrains s.r.o.
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
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.WebUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class StaticPageContentController extends BaseController {

  private static final Logger LOG = Logger.getInstance(StaticPageContentController.class.getName());

  private static final String PUBLIC_STATIC_CONTENT_PAGES_PATH = "/app/static_content/";
  private static final String FOLDER_NAME = "pages";
  private static final String ENCODING = "UTF-8";

  private final Configuration myConfig;

  public StaticPageContentController(@NotNull final AuthorizationInterceptor auth,
                                     @NotNull final WebControllerManager web,
                                     @NotNull final Configuration config) {
    myConfig = config;
    final String path = PUBLIC_STATIC_CONTENT_PAGES_PATH + "**";
    web.registerController(path, this);
    auth.addPathNotRequiringAuth(path);
  }

  @Override
  protected ModelAndView doHandle(@NotNull final HttpServletRequest request,
                                  @NotNull final HttpServletResponse response) throws Exception {


    String requestedResource = WebUtil.getPathFromUrl(WebUtil.getOriginalPathWithoutContext(request))
            .replace(PUBLIC_STATIC_CONTENT_PAGES_PATH, "");

    final char[] data;
    try {
      String requestedPath = myConfig.getIncludeFilesBase() + File.separator + FOLDER_NAME;
      File file = new File(requestedPath, requestedResource);
      data = FileUtil.loadFileText(file, ENCODING);
      LOG.info("Static content requested: " + file.getAbsolutePath());
      response.setContentType(WebUtil.getMimeType(request, file.getAbsolutePath()));
      response.getWriter().print(data);
    } catch (Exception e) {
      LOG.warn("Failed to retrieve file " + e.getMessage(), e);
      response.setContentType("text/plain");
      response.getWriter().write("ERROR: Content for StaticUIExtensions plugin was not found. Requested resource: " + requestedResource + ", error: " + e.getMessage());
      return null;
    }
    return null;
  }

}

