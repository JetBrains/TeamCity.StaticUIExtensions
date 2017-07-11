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

import jetbrains.buildServer.staticUIExtensions.model.Rule;
import jetbrains.buildServer.staticUIExtensions.model.StaticContent;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.SimplePageExtension;
import jetbrains.buildServer.web.util.WebUtil;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 19:44
 */
public class RulePageExtension extends SimplePageExtension {
  @NotNull
  private final Rule myRule;

  public RulePageExtension(@NotNull final PagePlaces pagePlaces,
                           @NotNull final PluginDescriptor descriptor,
                           @NotNull final ControllerPaths paths,
                           @NotNull final Rule rule) {
    super(pagePlaces);
    myRule = rule;

    setPlaceId(rule.getPlace());
    setPluginName(descriptor.getPluginName() + rule.getRuleId());


    final StaticContent content = rule.getContent();

    String includeUrl = paths.getResourceControllerBasePath();

    final String html = content.getHTML();
    if (html != null) {
      includeUrl = paths.addHtmlToResourceControllerPath(includeUrl, html);
    }

    final String js = content.getJS();
    if (js != null) {
      includeUrl = paths.addJsToResourceControllerPath(includeUrl, js);
    }

    final String css = content.getCSS();
    if (css != null) {
      includeUrl = paths.addCssToResourceControllerPath(includeUrl, css);
    }

    if (html == null && js == null && css == null) {
      includeUrl = paths.addEmptyContentControllerPath(includeUrl);
    }

    setIncludeUrl(includeUrl);
  }

  @Override
  public boolean isAvailable(@NotNull HttpServletRequest request) {
    if (!super.isAvailable(request)) return false;

    // consider using WebUtil.getOriginalRequestUrl since 8.1

    //TeamCity 8.0 compatible version:
//    String pathToMatch = WebUtil.getPathWithoutAuthenticationType(WebUtil.getPathWithoutContext(request, WebUtil.getRequestUrl(request).replace(".jsp", ".html")));

    //TeamCity 7.0 compatible version:
    String pathToMatch = WebUtil.getPathWithoutAuthenticationType(WebUtil.getPathWithoutContext(request, getRequestUrl(request).replace(".jsp", ".html")));

    if (pathToMatch.startsWith("/"))
      pathToMatch = pathToMatch.substring(1);

    return myRule.getUrlMatcher().matches(pathToMatch);
  }

  @NotNull
  public static String getRequestUrl(@NotNull HttpServletRequest request) {
    StringBuffer url = new StringBuffer(20);
    url.append(request.getRequestURI());
    if (request.getQueryString() != null && request.getQueryString().length() > 0) {
      url.append('?').append(request.getQueryString());
    }
    return url.toString();
  }

}
