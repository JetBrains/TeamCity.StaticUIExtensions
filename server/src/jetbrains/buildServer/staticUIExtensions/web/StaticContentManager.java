

package jetbrains.buildServer.staticUIExtensions.web;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 20:07
 */
public class StaticContentManager {
  private final Collection<RulePageExtension> myExtensions = new CopyOnWriteArrayList<RulePageExtension>();

  public synchronized void updateExtensions(@NotNull final Collection<RulePageExtension> newExtensions) {
    for (RulePageExtension extension : myExtensions) {
      extension.unregister();
    }

    myExtensions.clear();
    for (RulePageExtension newExtension : newExtensions) {
      newExtension.register();
      myExtensions.add(newExtension);
    }
  }
}