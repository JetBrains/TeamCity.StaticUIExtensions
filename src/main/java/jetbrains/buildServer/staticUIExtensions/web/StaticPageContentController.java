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
import jetbrains.buildServer.controllers.HttpDownloadProcessor;
import jetbrains.buildServer.staticUIExtensions.Configuration;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class StaticPageContentController extends StaticResourcesController {

  private static final Logger LOG = Logger.getInstance(StaticPageContentController.class.getName());

  private static final String PUBLIC_STATIC_CONTENT_PAGES_PATH = "/app/static_content/";
  private static final String FOLDER_NAME = "pages";

  public StaticPageContentController(@NotNull final AuthorizationInterceptor auth,
                                     @NotNull final WebControllerManager web,
                                     @NotNull final Configuration config,
                                     @NotNull final DownloadProcessor httpDownloadProcessor) {
    super(httpDownloadProcessor);
    final File container = FileUtil.getCanonicalFile(new File(config.getIncludeFilesBase(), FOLDER_NAME));
    if (!container.exists()) {
      try {
        if (!container.mkdirs() && !container.exists()) {
          LOG.error("Cannot create pages directory: " + container.getAbsolutePath());
        } else {
          LOG.debug("Created empty pages directory: " + container.getAbsolutePath());
        }
      } catch (Exception e) {
        LOG.error("Cannot create pages directory: " + container.getAbsolutePath(), e);
      }
    } else if (!container.isDirectory()) {
      LOG.warn("Pages directory is not a directory: " + container.getAbsolutePath());
    }
    setProvider(new StaticResourcesController.ResourceProvider() {
      @Nullable
      public Resource getResourceForPath(@NotNull String path) {
        path = path.replace(PUBLIC_STATIC_CONTENT_PAGES_PATH, "");
        if (!container.isDirectory() || !container.exists()) {
          return null;
        }
        final File file = FileUtil.getCanonicalFile(new File(container, path));
        if (!FileUtil.isAncestor(container, file, true)) {
          // Trying to access something not under container
          return null;
        }
        return new FileSystemResource(file);
      }
    });
    final String path = PUBLIC_STATIC_CONTENT_PAGES_PATH + "**";
    web.registerController(path, this);
    auth.addPathNotRequiringAuth(path);
  }

  @Nullable
  @Override
  protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
    Resource resource = getResourceToProcess(request, response);

    if (resource == null){
      return null;
    }

    return myHttpDownloadProcessor.processFileDownload(resource.getFile(), false, request, response);
  }
}

