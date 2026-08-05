

package jetbrains.buildServer.staticUIExtensions.web;

import jetbrains.buildServer.staticUIExtensions.Configuration;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 19:50
 */
public class ControllerPaths {
  private final String myResourcesControllerPath;
  @NotNull
  private final Configuration myConfig;

  public ControllerPaths(@NotNull final PluginDescriptor descriptor,
                         @NotNull final Configuration config) {
    myConfig = config;
    myResourcesControllerPath = descriptor.getPluginResourcesPath("resources.html");
  }

  public String getResourceControllerRegistrationBase() {
    return myResourcesControllerPath;
  }

  @NotNull
  public String getTokenParameter() {
    return "token";
  }

  @NotNull
  public String getIncludeFileParameter() {
    return "includeFile";
  }

  @NotNull
  public String getIncludeCssFileParameter() {
    return "includeCssFile";
  }

  @NotNull
  public String getIncludeJsFileParameter() {
    return "includeJsFile";
  }

  public String getEmptyContentParameter() {
    return "showEmptyContent";
  }

  @NotNull
  public String getResourceControllerBasePath() {
    return getResourceControllerRegistrationBase() + "?" + getTokenParameter() + "=" + myConfig.getAccessToken();
  }

  @NotNull
  public String addHtmlToResourceControllerPath(@NotNull String path, @NotNull String resource) {
    return path + "&" + getIncludeFileParameter() + "=" + resource;
  }

  @NotNull
  public String addCssToResourceControllerPath(@NotNull String path, @NotNull String resource) {
    return path + "&" + getIncludeCssFileParameter() + "=" + resource;
  }

  @NotNull
  public String addJsToResourceControllerPath(@NotNull String path, @NotNull String resource) {
    return path + "&" + getIncludeJsFileParameter() + "=" + resource;
  }

  @NotNull
  public String addEmptyContentControllerPath(@NotNull String path) {
    return path + "&" + getEmptyContentParameter() + "=42";
  }


}