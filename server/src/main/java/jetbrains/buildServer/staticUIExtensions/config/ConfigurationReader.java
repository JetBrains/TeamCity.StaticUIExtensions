package jetbrains.buildServer.staticUIExtensions.config;

import jetbrains.buildServer.staticUIExtensions.ConfigurationException;
import jetbrains.buildServer.staticUIExtensions.model.Rule;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 17.11.11 18:59
 */
public interface ConfigurationReader {
  @NotNull
  Collection<Rule> parseConfiguration(@NotNull File config) throws ConfigurationException;
}
