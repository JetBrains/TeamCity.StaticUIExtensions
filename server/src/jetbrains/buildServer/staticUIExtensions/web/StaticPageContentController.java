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


import jetbrains.buildServer.controllers.AuthorizationInterceptor;
import jetbrains.buildServer.staticUIExtensions.Configuration;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.WebUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class StaticPageContentController extends BasePageContentController {


  private static final String LOCAL_FOLDER_NAME = "pages";

  @NotNull
  protected final Configuration config;


  /**
   * @param auth                - from standard TeamCity context
   * @param web                 - from standard TeamCity context
   * @param config              - from standard TeamCity context
   * @param pagesContentUrlBase - URL part that will be used to create controller mapping
   */
  public StaticPageContentController(@NotNull AuthorizationInterceptor auth,
                                     @NotNull WebControllerManager web,
                                     @NotNull Configuration config,
                                     @NotNull String pagesContentUrlBase) {
    super(auth, web, pagesContentUrlBase);
    this.config = config;
  }

  @Override
  protected void writeResourceContent(@NotNull HttpServletRequest request,
                                      @NotNull HttpServletResponse response,
                                      @NotNull File file) throws IOException {
    FileCopyUtils.copy(new FileReader(file), response.getWriter());
  }


  @Override
  protected File getRequestedFile(@NotNull String requestedResource, @NotNull String requestedResourceBasePath) {
    return new File(requestedResourceBasePath, requestedResource);
  }

  @Override
  protected String getResourceBaseFolder() {
    return config.getIncludeFilesBase() + File.separator + LOCAL_FOLDER_NAME;
  }


  @Override
  protected boolean isLegalPath(@NotNull File maybeChild, @NotNull File possibleParent) throws IOException {
    if (!maybeChild.exists()) {
      return false;
    }
    final File parent = possibleParent.getCanonicalFile();
    if (!parent.exists() || !parent.isDirectory()) {
      return false;
    }

    File child = maybeChild.getCanonicalFile();
    while (child != null) {
      if (child.equals(parent)) {
        return true;
      }
      child = child.getParentFile();
    }
    return false;
  }
}

