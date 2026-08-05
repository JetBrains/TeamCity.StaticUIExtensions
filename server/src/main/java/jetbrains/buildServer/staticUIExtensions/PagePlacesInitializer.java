package jetbrains.buildServer.staticUIExtensions;

import jetbrains.buildServer.staticUIExtensions.model.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 17.11.11 18:58
 */
public interface PagePlacesInitializer {
  void registerPagePlaces(@NotNull Collection<Rule> rules);
}
