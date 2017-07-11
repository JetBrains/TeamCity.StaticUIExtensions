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
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.web.util.WebUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.LastModified;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Static resources controller. E.g. for static files serving.
 * <p/>
 * Usage:
 * You can create subtype or instantiate as a Spring bean with special ResourceProvider.
 * <p/>
 * ResourceProvider can be set both from constructor and as a parameter.
 *
 * @author Vladislav.Rassokhin
 */
public class StaticResourcesController extends BaseController implements LastModified {
  @NotNull
  protected final DownloadProcessor myHttpDownloadProcessor;

  public static interface ResourceProvider {
    @Nullable
    public Resource getResourceForPath(@NotNull final String path);
  }

  private static final Logger LOG = Logger.getInstance(StaticResourcesController.class.getName());

  private ResourceProvider myProvider;

  public StaticResourcesController(@NotNull final DownloadProcessor httpDownloadProcessor) {
    myHttpDownloadProcessor = httpDownloadProcessor;
  }

  public void setProvider(@NotNull final ResourceProvider provider) {
    myProvider = provider;
  }

  public long getLastModified(@NotNull final HttpServletRequest request) {
    final Resource resource = getResource(request);
    if (resource != null) {
      try {
        return resource.lastModified();
      } catch (IOException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Cannot check last modification date for resource " + resource, e);
        }
      }
    }
    return -1;
  }

  @Nullable
  @Override
  protected ModelAndView doHandle(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response) throws Exception {

    Resource resource = getResourceToProcess(request, response);

    if (resource == null){
      return null;
    }

    return myHttpDownloadProcessor.processFileDownload(resource.getFile(), true, request, response);
  }

  @Nullable
  protected Resource getResourceToProcess(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response) throws Exception {
    if (myProvider == null) {
      throw new IllegalStateException("ResourcesProvider for StaticResourcesController [" + this + "] is null. Cannot dispatch request.");
    }

    // check whether a matching resource exists
    final Resource resource = getResource(request);
    if (resource == null) {
      LOG.debug("No matching resource found - returning 404");
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }

    // check not modified
    if (new ServletWebRequest(request, response).checkNotModified(resource.lastModified())) {
      LOG.debug("Resource not modified - returning 304");
      return null;
    }

    return resource;
  }

  @Nullable
  protected Resource getResource(@NotNull final HttpServletRequest request) {
    if (myProvider == null) {
      return null;
    }
    final String path = WebUtil.getPathFromUrl(WebUtil.getOriginalPathWithoutAuthenticationType(request));

    if (LOG.isDebugEnabled()) {
      LOG.debug("Trying relative path [" + path + "]");
    }
    final Resource resource = myProvider.getResourceForPath(path);
    if (resource != null && resource.exists() && resource.isReadable()) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Found matching resource: " + resource);
      }
      return resource;
    } else if (LOG.isDebugEnabled()) {
      if (resource == null) {
        LOG.debug("Relative resource not found");
      } else {
        LOG.debug("Relative resource doesn't exist or isn't readable: " + resource);
      }
    }
    return null;
  }
}
