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
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.WebUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * This class also use in TeamCity.Widgets plugin
 * https://github.com/JetBrains/TeamCity.Widgets
 */
public abstract class BasePageContentController extends BaseController {

  private static final Logger LOG = Logger.getInstance(BasePageContentController.class.getName());

  @NotNull
  private final String pagesContentUrlBase;

  /**
   * @param auth                - from standard TeamCity context
   * @param web                 - from standard TeamCity context
   * @param pagesContentUrlBase - URL part that will be used to create controller mapping
   *                            e.g. /app/static_content/
   */
  public BasePageContentController(@NotNull final AuthorizationInterceptor auth,
                                   @NotNull final WebControllerManager web,
                                   @NotNull final String pagesContentUrlBase) {
    this.pagesContentUrlBase = pagesContentUrlBase;
    web.registerController(getControllerRegistrationPath(), this);
    auth.addPathNotRequiringAuth(getControllerRegistrationPath());
  }

  private String getControllerRegistrationPath() {
    if (!pagesContentUrlBase.endsWith("/")) {
      throw new IllegalArgumentException("pagesContentUrlBase should have / at the end");
    }
    return pagesContentUrlBase + "**";
  }

  @Override
  protected ModelAndView doHandle(@NotNull final HttpServletRequest request,
                                  @NotNull final HttpServletResponse response) throws Exception {


    String requestedResource = WebUtil.getPathFromUrl(WebUtil.getOriginalPathWithoutContext(request))
            .replace(pagesContentUrlBase, "");

    try {
      String requestedResourceBasePath = getResourceBaseFolder();
      File file = getRequestedFile(requestedResource, requestedResourceBasePath);
      boolean legal = isLegalPath(file, new File(requestedResourceBasePath));
      if (!legal) {
        throw new IllegalArgumentException("Requested resource doesn't exist under the resource base folder.");
      }
      response.setContentType(WebUtil.getMimeType(request, file.getName()));
      writeResourceContent(request, response, file);
    } catch (Exception e) {
      LOG.warn("Failed to retrieve file. Error: " + e.getMessage(), e);
      response.setContentType("text/plain");
      response.getWriter().write("ERROR: Content for StaticUIExtension plugin was not found. " +
              "Requested resource: " + requestedResource + ", error: " + e.getMessage());
    }
    return null;
  }

  protected abstract void writeResourceContent(@NotNull HttpServletRequest request,
                                               @NotNull HttpServletResponse response,
                                               @NotNull File file) throws IOException;


  protected abstract String getResourceBaseFolder();

  protected abstract File getRequestedFile(@NotNull String requestedResource, @NotNull String requestedResourceBasePath);


  protected abstract boolean isLegalPath(@NotNull File maybeChild, @NotNull File possibleParent) throws IOException;

}

