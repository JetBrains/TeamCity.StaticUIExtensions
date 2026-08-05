

package jetbrains.buildServer.staticUIExtensions.web;

import jetbrains.buildServer.staticUIExtensions.model.Rule;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 19:58
 */
public class RulePageExtensionsFactory {
  private final PagePlaces myPlaces;
  private final PluginDescriptor myDescription;
  private final ControllerPaths myPaths;

  public RulePageExtensionsFactory(@NotNull final PagePlaces places,
                                   @NotNull final PluginDescriptor description,
                                   @NotNull final ControllerPaths paths) {
    myPlaces = places;
    myDescription = description;
    myPaths = paths;
  }

  @NotNull
  public RulePageExtension createExtension(@NotNull final Rule rule) {
    return new RulePageExtension(myPlaces, myDescription, myPaths, rule);
  }
}