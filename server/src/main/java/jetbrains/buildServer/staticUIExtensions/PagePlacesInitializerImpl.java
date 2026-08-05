

package jetbrains.buildServer.staticUIExtensions;

import jetbrains.buildServer.staticUIExtensions.model.Rule;
import jetbrains.buildServer.staticUIExtensions.web.RulePageExtension;
import jetbrains.buildServer.staticUIExtensions.web.RulePageExtensionsFactory;
import jetbrains.buildServer.staticUIExtensions.web.StaticContentManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 20:11
 */
public class PagePlacesInitializerImpl implements PagePlacesInitializer {
  private final RulePageExtensionsFactory myExtensionsFactory;
  private final StaticContentManager myRegistry;

  public PagePlacesInitializerImpl(@NotNull final RulePageExtensionsFactory extensionsFactory,
                                   @NotNull final StaticContentManager registry) {
    myExtensionsFactory = extensionsFactory;
    myRegistry = registry;
  }

  public void registerPagePlaces(@NotNull final Collection<Rule> rules) {
    List<RulePageExtension> exts = new ArrayList<RulePageExtension>();
    for (Rule rule : rules) {
      exts.add(myExtensionsFactory.createExtension(rule));
    }
    myRegistry.updateExtensions(exts);
  }
}